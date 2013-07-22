require 'pg'
require 'nokogiri'
require './lib/site_api'
require 'pry'

class BlogImporter
  def initialize(filename, site_api, wp_processor)
    @filename = filename
    @site_api = site_api
    @wp_processor = wp_processor
  end

  def import
    puts "\n\nImporting #{@filename}\n---------\n "
    puts "\nLoading authors: "
    import_authors
    puts "\n\nLoading posts: "
    import_posts
    puts "\n\nImport complete.\n"
  end

  def import_authors
    authors = xml_doc.xpath("//wp:author")
    authors.each do |author|
      print_progress
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
    post_elements = xml_doc.xpath("//item[wp:status/text()='publish'][wp:post_type/text()='post']")
    post_elements.each_with_index do |element, i|
      post_date = element.xpath('wp:post_date_gmt').text
      content = element.xpath('content:encoded').text
      title = element.xpath('title').text
      puts "\nImporting: #{i + 1} - #{title}"
      processed_content = ""
      content.split("\n").each do |line|
        processed_content << @wp_processor.processLine(line) << "\n"
      end
      processed_content = processed_content[0..-2]

      hash = {
          title: title,
          content: processed_content,
          category: 'ENGINEERING',
          createdAt: post_date,
          publishAt: post_date,
          authorMemberId: element.xpath('dc:creator').text
      }
      data = hash

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

    if (@xml_doc.errors.size > 0)
      puts "WARNING: Errors in XML import file: #{@xml_doc.errors}"
      System.exit(1)
    end

    @xml_doc
  end

  def blog_file_name
    File.exists?(FULL_BLOG_FILE) ? FULL_BLOG_FILE : TEST_BLOG_FILE
  end
end


