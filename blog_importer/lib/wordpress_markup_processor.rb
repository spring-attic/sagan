# encoding: UTF-8

require 'htmlentities'

class WordpressMarkupProcessor

  def process(content)
    convert_incorrectly_encoded_characters(convert_code_blocks(content))
  end

  def convert_code_blocks(content)
    content_marker = "(.*?)"
    closing_marker = "\n?\\[\\/(?:source|sourcecode|groovy|html|java|python|scala|xml|coldfusion|js|plain|text|code|CODE)\\](?:\\s*?</pre>)?"

    supported_attributes_marker = "(?:<pre[^>]*>\\s*?)?\\[(?:code|source|sourcecode) lang\\w*=\"(\\w+)\"[^\\]]*\\]"
    supported_attributes = Regexp.new(supported_attributes_marker + content_marker + closing_marker, Regexp::MULTILINE)
    content = replace_no_attributes(content, supported_attributes)

    unsupported_attributes_marker = "(?:<pre[^>]*>\\s*?)?\\[(groovy|html|java|python|scala|xml|coldfusion|js|plain|text|code|CODE|source|sourcecode)(?: [^\\]]+)?\\]\n?"
    unsupported_attributes = Regexp.new(unsupported_attributes_marker + content_marker + closing_marker, Regexp::MULTILINE)
    replace_no_attributes(content, unsupported_attributes)
  end

  def convert_incorrectly_encoded_characters(content)
    content.gsub('â€™', '&apos;').gsub("\xc3\xa2\xc2\x80\xc2\x9c", "&ldquo;").gsub("\xc3\xa2\xc2\x80\x3f", "&rdquo;")
      .gsub("\xC3\xA2\xC2\x80\xC2\x99", "&apos;")
  end

  def replace_no_attributes(content, regexp)
    content.gsub(regexp) do
      language = $1.downcase
      puts "Translated #{language} marker"
      body = HTMLEntities.new.decode($2)
      "\n```#{language}\n" + body + "\n```\n"
    end
  end

end