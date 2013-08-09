require 'nokogiri'
require './lib/site_api'
require 'pry'

module Wordpress
  class BlogImporter
    def initialize(filename, site_api, wp_processor)
      @filename = filename
      @site_api = site_api
      @wp_processor = wp_processor
    end

    def import(mappings_io)
      puts "\n\nImporting #{@filename}\n---------\n "
      puts "\nLoading authors: "
      import_authors
      puts "\n\nLoading posts: "
      mappings = import_posts
      puts "\n\nImport complete.\n"
      save_mappings(mappings, mappings_io)
    end

    def import_authors
      authors = xml_doc.xpath("//wp:author")
      authors.each do |author|
        username = author.xpath('wp:author_login').text.downcase.gsub(' ', '_')
        puts "Importing #{username}"
        data = {
            username: username,
            gravatarEmail: author.xpath('wp:author_email').text,
            name: author.xpath('wp:author_display_name').text
        }
        @site_api.save_member_profile data
      end
    end

    def import_posts
      mappings = []
      post_elements.each_with_index do |element, i|
        post_data = extract_post_data element

        puts "\nImporting: #{i + 1} - #{post_data[:title]}"

        process_content(post_data)

        response = @site_api.save_blog_post(create_post_request(post_data))
        if response.code >= 400
          puts "Error importing blog post: #{response.body}"
          p create_post_request(post_data)
          # TODO - change create post URI to include member name
        end

        mapping = {
            "old_url" => post_data[:link],
            "new_url" => response.headers["Location"]
        }
        mappings << mapping
      end
      mappings
    end

    def post_elements
      xml_doc.xpath("//item[wp:status/text()='publish'][wp:post_type/text()='post']")
    end

    def save_mappings(mappings, mappings_io)
      mappings_io.puts mappings.to_yaml
    end

    def create_post_request(post_data)
      {
          title: post_data[:title],
          content: post_data[:content],
          category: 'ENGINEERING',
          createdAt: post_data[:post_date],
          publishAt: post_data[:post_date],
          username: post_data[:creator]
      }
    end

    def extract_post_data element
      post_date = element.xpath('wp:post_date_gmt').text
      post_date = post_date.gsub(/:\d\d$/, "")
      {
          link: element.xpath('link').text,
          post_date: post_date,
          content: element.xpath('content:encoded').text,
          title: element.xpath('title').text,
          creator: element.xpath('dc:creator').text
      }
    end

    def process_content(post_data)
      post_data[:content] = @wp_processor.process(post_data[:content])
    end

    def xml_doc
      @xml_doc ||= File.open(@filename) do |f|
        Nokogiri::XML(open(f))
      end

      if (@xml_doc.errors.size > 0)
        puts "WARNING: Errors in XML import file: #{@xml_doc.errors}"
        exit(1)
      end

      @xml_doc
    end

    def blog_file_name
      File.exists?(FULL_BLOG_FILE) ? FULL_BLOG_FILE : TEST_BLOG_FILE
    end
  end
end

