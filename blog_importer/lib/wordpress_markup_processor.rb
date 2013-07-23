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
    no_attributes_marker = /\[(groovy|html|java|python|scala|xml|coldfusion|js|plain|text|code|CODE)\]\n?/
    content = replace_codeblock_with_markdown(content, no_attributes_marker)

    supported_attributes_marker = /\[(?:code|source|sourcecode) lang\w*="(\w+)"[^]]*\]/
    content = replace_codeblock_with_markdown(content, supported_attributes_marker)

    unsupported_attributes_marker = /\[(groovy|html|java|python|scala|xml|coldfusion|js|plain|text|code|CODE|source|sourcecode) [^]]+\]\n?/
    content = replace_codeblock_with_markdown(content, unsupported_attributes_marker)

    closing_marker = /\n?\[\/(?:source|sourcecode|groovy|html|java|python|scala|xml|coldfusion|js|plain|text|code|CODE)\]/
    content.gsub(closing_marker) do |match|
      puts "Closed marker #{match}\n\n"
      "\n```"
    end
  end

  def replace_codeblock_with_markdown(line, regexp)
    line.gsub(regexp) do |wp_marker|
      language = $1.downcase
      md_marker = "\n```#{language}\n"
      puts "From >>> #{wp_marker}\n To  >>> #{md_marker}"
      md_marker
    end
  end

end