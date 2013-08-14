require 'csv'
require 'uri'

module Redirect
  class Importer

    def initialize(vars)
      @vars = vars
    end

    def import(io)
      csv = CSV.new(io, :headers => :first_row).each
      rows = filter_non_redirects(csv)
      rows = substitute_url_variables(rows)
      rows = filter_invalid_redirect_uris(rows)
      rows = transform_into_xml_rules(rows)
      rows.to_a.join("")
    end

    def filter_non_redirects(enumerator)
      filter_enumerator(enumerator) {|row| row["Redirect? default=no"] == "yes" }
    end

    def substitute_url_variables(enumerator)
      transform_enumerator(enumerator) do |row|
        url = row["Redirect Target URI"]
        if url =~ /({(\w+)})/
          variable_name = $2
          value = @vars[variable_name]
          raise("Missing Variable: #{variable_name}") unless value
          row["Redirect Target URI"] = url.gsub($1, value)
        end
        row
      end
    end

    def filter_invalid_redirect_uris(enumerator)
      filter_enumerator(enumerator) {|row| is_valid_uri?(row["Redirect Target URI"]) && is_valid_uri?(row["URL"]) }
    end

    def transform_into_xml_rules(enumerator)
      transform_enumerator(enumerator) do |row|
        old_uri = URI.parse(row["URL"])
        <<-XML
<rule>
  <condition name="host">#{old_uri.host}</condition>
  <from>^#{old_uri.path}$</from>
  <to type="permanent-redirect" last="true">#{row["Redirect Target URI"]}</to>
</rule>
        XML
      end
    end

    private
    def is_valid_uri?(uri)
      uri && uri.start_with?("http://")
    end

    def filter_enumerator(enumerator, &block)
      Enumerator.new do |yielder|
        enumerator.each do |row|
          yielder.yield row if block.call(row)
        end
      end
    end

    def transform_enumerator(enumerator, &block)
      Enumerator.new do |yielder|
        enumerator.each do |row|
          yielder.yield block.call(row)
        end
      end
    end

  end
end