require 'nokogiri'

mappings = YAML.load(File.new("/Users/pivotal/workspace/sagan/springframework.org/src/main/resources/redirect_mappings.yml"))

builder = Nokogiri::XML::Builder.new do |xml|
  xml.urlrewrite("use-query-string" => true) do
    mappings["mappings"].each do |mapping|
      xml.rule do
        xml.condition("www.springsource.org", :name => "host")
        xml.from URI.parse(mapping["old_url"]).path
        xml.to("http://springframework.io#{URI.parse(mapping["new_url"]).path}", {:type => "permanent-redirect", :last => "true"})
      end
    end
  end
end

puts builder.to_xml
