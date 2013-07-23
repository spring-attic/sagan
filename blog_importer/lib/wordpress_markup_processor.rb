require 'htmlentities'

class WordpressMarkupProcessor

  def process(input_filename, output_filename)
    File.open(output_filename, 'w') do |output|
      File.open(input_filename) do |input|
        input.each do |line|
          output.write(process(line))
        end
      end
    end
  end

  def process content
    content_marker = "(.*?)"
    closing_marker = "\\[\\/(?:source|sourcecode|groovy|html|java|python|scala|xml|coldfusion|js|plain|text|code|CODE)\\]"

    supported_attributes_marker = "\\[(?:code|source|sourcecode) lang\\w*=\"(\\w+)\"[^\\]]*\\]"
    supported_attributes = Regexp.new(supported_attributes_marker + content_marker + closing_marker, Regexp::MULTILINE)
    content = replace_no_attributes(content, supported_attributes)

    unsupported_attributes_marker = "\\[(groovy|html|java|python|scala|xml|coldfusion|js|plain|text|code|CODE|source|sourcecode)(?: [^\\]]+)?\\]\n?"
    unsupported_attributes = Regexp.new(unsupported_attributes_marker + content_marker + closing_marker, Regexp::MULTILINE)
    replace_no_attributes(content, unsupported_attributes)
  end

  def replace_no_attributes(content, regexp)
    content.gsub(regexp) do
      language = $1.downcase
      puts "Translated #{language} marker"
      body = HTMLEntities.new.decode($2)
      "\n```#{language}\n" + body + "\n```"
    end
  end

end