require 'nokogiri'
require 'yaml'

module Drupal
  class BlogImporter

    CATEGORIES = {
        "blog-news" => "NEWS_AND_EVENTS",
        "blog-engineering" => "ENGINEERING",
        "blog-releases" => "RELEASES"
    }

    def initialize(xml_filename, siteapi, authors)
      @filename = xml_filename
      @siteapi = siteapi
      @authors = authors
      @error_count = 0
    end

    def import(io)
      mappings = []

      post_elements.each_with_index do |element, i|
        puts "Importing post #{i}"
        if element.xpath("body").text.nil? || element.xpath("body").text == ""
          puts "Skipping empty post"
          next
        end
        response = @siteapi.save_blog_post(post_data(element))

        if response.code >= 400
          puts "Error importing blog post: #{response.body}"
          p post_data(element)
        end

        mapping = {
            "old_url" => element.xpath('link').text,
            "new_url" => response.headers["Location"]
        }
        mappings << mapping

      end

      io.puts mappings.to_yaml
    end

    def post_data(element)
      author = extract_author(element)
      category = CATEGORIES[element.xpath("category").text]
      publish_date = Time.at(element.xpath("createdTimestamp").text.to_i).utc.strftime("%Y-%m-%d %H:%M")
      {
          title: element.xpath("title").text,
          content: element.xpath("body").text,
          category: category,
          publishAt: publish_date,
          createdAt: publish_date,
          username: author
      }
    end

    def extract_author(element)
      author_name = element.xpath("author").text
      author_id = @authors[author_name]
      if author_id.nil? && @authors.values.include?(author_name)
        author_id = author_name
      end

      if author_id == nil
        author_id = author_name.gsub(" ", "_").downcase
        data = {
            username: author_id,
            name: author_name
        }
        @siteapi.save_member_profile data
        @authors[author_name] = author_id
      end
      author_id
    end

    def post_elements
      xml_doc.xpath("//exportedItems/item")
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
  end
end