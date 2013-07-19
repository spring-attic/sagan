require 'pg'
require 'nokogiri'
require 'httparty'

class SiteApi
  def initialize input_base_uri
    @base_uri = input_base_uri
  end

  def save_member_profile(options)
    HTTParty.post("http://#{@base_uri}/migration/profile", {body: options})
  end

  def save_blog_post(options)
    HTTParty.post("http://#{@base_uri}/migration/blogpost", {body: options})
  end
end

class BlogImporter
  def initialize(filename, site_api)
    @filename = filename
    @site_api = site_api
  end

  def import
    import_authors
    import_posts
  end

  def import_authors
    authors = xml_doc.xpath("//wp:author")
    authors.each do |author|
      data = {
          memberId: author.xpath('wp:author_login').text,
          githubUsername: author.xpath('wp:author_login').text,
          gravatarEmail: author.xpath('wp:author_email').text,
          name: author.xpath('wp:author_display_name').text
      }
      @site_api.save_member_profile data
    end
  end

  def import_posts
    puts "Importing #{@filename} "
    post_elements = xml_doc.xpath("//item[wp:status/text()='publish'][wp:post_type/text()='post']")
    post_elements.each do |element|
      print_progress
      post_date = element.xpath('wp:post_date_gmt').text
      data = {
          title: element.xpath('title').text,
          content: element.xpath('content:encoded').text,
          category: 'ENGINEERING',
          createdAt: post_date,
          publishAt: post_date,
          authorMemberId: element.xpath('dc:creator').text
      }

      @site_api.save_blog_post data
    end
    true
  end

  def print_progress
    print "~"
    $stdout.flush
  end


  def xml_doc
    @xml_doc ||= File.open(@filename) do |f|
      Nokogiri::XML(open(f))
    end
  end

  def blog_file_name
    File.exists?(FULL_BLOG_FILE) ? FULL_BLOG_FILE : TEST_BLOG_FILE
  end
end


