
class ExportCleaner

  def clean(input_filename, output_filename)
    cdata_stack = 0
    File.open(output_filename, 'w') do |output|
      File.open(input_filename) do |input|
        input.each do |line|

          unless line.index('<![CDATA[').nil?
            if (inside_cdata(cdata_stack))
              line = line.gsub('<![CDATA[', '&lt;![CDATA[')
            end
            cdata_stack += 1
          end

          unless line.index(']]>').nil?
            if (inside_encoded_cdata(cdata_stack))
              line = line.gsub(']]>', ']]&gt;')
            end
            cdata_stack -= 1
          end

          # ---

          line = strip_control_k_and_c(line)
          output.write(line)
          #output.write("#{line}\n")
        end

      end
    end

  end

  def inside_encoded_cdata(cdata_stack)
    cdata_stack > 1
  end

  def inside_cdata(cdata_stack)
    cdata_stack > 0
  end

  def strip_control_k_and_c(input)
    input.chars.inject("") do |str, char|
      if char.ascii_only? and (char.ord == 3 or char.ord == 11)
        str << ' '
      else
        str << char
      end
      str
    end
  end

end