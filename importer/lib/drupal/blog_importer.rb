require 'nokogiri'

module Drupal
  class BlogImporter
    def initialize(xml_filename, siteapi, authors)
      @filename = xml_filename
      @siteapi = siteapi
      @authors = authors
    end

    def import(io)

      post_elements.each_with_index do |element, i|
        @siteapi.save_blog_post(post_data(element))
      end

    end

    def post_data(element)
      author = @authors[element.xpath("author").text] || 'fixme'
      category = element.xpath("category").text.gsub("blog-", "").upcase
      publish_date = Time.at(element.xpath("createdTimestamp").text.to_i).utc.strftime("%Y-%m-%d %H:%M")
      {
          title: element.xpath("title").text,
          content: element.xpath("body").text,
          category: category,
          publishAt: publish_date,
          createdAt: publish_date,
          authorMemberId: author
      }
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