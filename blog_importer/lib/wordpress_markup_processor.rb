class WordpressMarkupProcessor

  def process(input_filename, output_filename)
    File.open(output_filename, 'w') do |output|
      File.open(input_filename) do |input|
        input.each do |line|
          output.write(processLine(line))
        end
      end
    end
  end

  def processLine line
    no_attributes_marker = /(.*)\[(groovy|html|java|python|scala|xml|coldfusion|js|plain|text|code|CODE)\]\n?/
    line = replace_codeblock_with_markdown(line, no_attributes_marker)

    supported_attributes_marker = /(.*)\[(?:code|source|sourcecode) lang.*="([^"]*)"\]/
    line = replace_codeblock_with_markdown(line, supported_attributes_marker)

    unsupported_attributes_marker = /(.*)\[(groovy|html|java|python|scala|xml|coldfusion|js|plain|text|code|CODE|source|sourcecode)[^]]+\]\n?/
    line = replace_codeblock_with_markdown(line, unsupported_attributes_marker)

    closing_marker = /\n?\[\/(?:source|sourcecode|groovy|html|java|python|scala|xml|coldfusion|js|plain|text|code|CODE)\]/
    line.gsub(closing_marker) do |match|
      puts "Closed marker #{match}"
      "\n```"
    end
  end

  def replace_codeblock_with_markdown(line, regexp)
    line.gsub(regexp) do |wp_marker|
      preceding_content = $1.strip
      language = $2.downcase
      if preceding_content == ""
        md_marker = "```#{language}\n"
      else
        md_marker = "#{preceding_content}\n```#{language}\n"
      end
      puts "Processed #{wp_marker} to #{md_marker}"
      md_marker
    end
  end

end