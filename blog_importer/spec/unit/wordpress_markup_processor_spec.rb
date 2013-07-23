require 'rspec'
require './lib/wordpress_markup_processor'

describe WordpressMarkupProcessor do

  let(:processor) { WordpressMarkupProcessor.new }

  it "converts inline markers to backticks" do
    processor.process('[plain]Here is some content[/plain]').should == "\n```plain\nHere is some content\n```\n"
  end

  it "converts multiline markers to backticks" do
    processor.process("[plain]Here is some content\n[/plain]").should == "\n```plain\nHere is some content\n```\n"
    processor.process("[plain]\nHere is some content[/plain]").should == "\n```plain\nHere is some content\n```\n"
    processor.process("[plain]\nHere is some content\n[/plain]").should == "\n```plain\nHere is some content\n```\n"
  end

  it "converts markers in the middle of a line to backticks" do
    processor.process('Here is some text [plain]Here is some content[/plain]').should == "Here is some text \n```plain\nHere is some content\n```\n"
    processor.process("Here is some text [plain]Here is \nsome content[/plain]").should == "Here is some text \n```plain\nHere is \nsome content\n```\n"
  end

  it "converts markers with content after the close" do
    processor.process('Here is some text [plain]Here is some content[/plain] some content here').should == "Here is some text \n```plain\nHere is some content\n```\n some content here"
  end

  it "converts more than one marker in the same content" do
    input = <<-INPUT
Here is some text
[plain]Here is some content[/plain]
some content here
[java]
public class Foo{}
[/java]Bye!
    INPUT


    expected_output = <<-OUTPUT
Here is some text

```plain
Here is some content
```

some content here

```java
public class Foo{}
```
Bye!
    OUTPUT

    processor.process(input).should == expected_output
  end

  describe "converts the language marker" do
    ["plain", "groovy", "html", "java", "python", "scala", "xml", "coldfusion", "js", "plain", "text", "code", "CODE"].each do |language|
      it "#{language}" do
        processor.process("[#{language}]Here is some content\n[/#{language}]").should == "\n```#{language.downcase}\nHere is some content\n```\n"
      end
    end
  end

  it "does not convert arbitrary markers" do
    processor.process('[something]Here is some content[/something]').should == "[something]Here is some content[/something]"
  end

  it "does not partially match markers" do
    processor.process('No unique bean of type [javax.sql.DataSource]').should == "No unique bean of type [javax.sql.DataSource]"
  end

  describe "converts the marker " do
    ["code", "source", "sourcecode"].each do |marker|

      describe "'#{marker}' with the attribute" do

        ["lang", "language"].each do |supported_attribute|
          it "'#{supported_attribute}' and converts it to markdown language syntax" do
            processor.process("[#{marker} #{supported_attribute}=\"java\"]public class Foo{}[/#{marker}]").should == "\n```java\npublic class Foo{}\n```\n"
          end

          it "'#{supported_attribute}' and converts it to lower case markdown language syntax" do
            processor.process("[#{marker} #{supported_attribute}=\"JAVA\"]public class Foo{}[/#{marker}]").should == "\n```java\npublic class Foo{}\n```\n"
          end

          it "'#{supported_attribute}' and converts it ignoring additional attributes" do
            processor.process("[#{marker} #{supported_attribute}=\"java\" other_attribute=\"other\"]public class Foo{}[/#{marker}]").should == "\n```java\npublic class Foo{}\n```\n"
          end

        end
      end
    end
  end

  it "converts markers with unsupported attributes to markdown syntax and ignores the attributes" do
    processor.process('[groovy highlight="10,15"]public class Foo{}[/groovy]').should == "\n```groovy\npublic class Foo{}\n```\n"
    processor.process('[source light="true"]public class Foo{}[/source]').should == "\n```source\npublic class Foo{}\n```\n"
  end

  it "unencodes HTML entities inside the code markers" do


    input = <<-INPUT
&lt;Text outside does not have &quot;entities&quot; escaped&gt;
[xml]
&lt;filter-mapping&gt;
  &lt;filter-name&gt;springSecurityFilterChain&lt;/filter-name&gt;
  &lt;url-pattern&gt;/*&lt;/url-pattern&gt;
  &lt;dispatcher&gt;&quot;ERROR&quot;&lt;/dispatcher&gt;
  &lt;dispatcher&gt;REQUEST&amp;TEST&lt;/dispatcher&gt;
&lt;/filter-mapping&gt;
[/xml]
&amp; its done!
    INPUT

    expected = <<-EXPECTED
&lt;Text outside does not have &quot;entities&quot; escaped&gt;

```xml
<filter-mapping>
  <filter-name>springSecurityFilterChain</filter-name>
  <url-pattern>/*</url-pattern>
  <dispatcher>"ERROR"</dispatcher>
  <dispatcher>REQUEST&TEST</dispatcher>
</filter-mapping>
```

&amp; its done!
    EXPECTED

    processor.process(input).should == expected
  end

  describe "enclosing <pre> tags" do
    it "directly adjacent to the marker are stripped" do
      processor.process("Hi <pre>[source]Some code[/plain]</pre> bye").should == "Hi \n```source\nSome code\n```\n bye"
    end
    it "on the line preceding the marker are stripped" do
      processor.process("Hi <pre>\n\t[source]Some code[/plain] \n</pre> bye").should == "Hi \n```source\nSome code\n```\n bye"
    end
  end

end
