require 'rspec'
require './lib/wordpress_markup_processor'

describe WordpressMarkupProcessor do

  let(:processor) { WordpressMarkupProcessor.new }
  
  it "converts inline markers to backticks" do
    processor.process('[plain]Here is some content[/plain]').should == "```plain\nHere is some content\n```"
  end

  it "converts multiline markers to backticks" do
    processor.process("[plain]Here is some content\n[/plain]").should == "```plain\nHere is some content\n```"
    processor.process("[plain]\nHere is some content[/plain]").should == "```plain\nHere is some content\n```"
    processor.process("[plain]\nHere is some content\n[/plain]").should == "```plain\nHere is some content\n```"
  end

  it "converts markers in the middle of a line to backticks" do
    processor.process('Here is some text [plain]Here is some content[/plain]').should == "Here is some text\n```plain\nHere is some content\n```"
    processor.process("Here is some text [plain]Here is \nsome content[/plain]").should == "Here is some text\n```plain\nHere is \nsome content\n```"
  end

  it "converts markers with content after the close" do
    processor.process('Here is some text [plain]Here is some content[/plain] some content here').should == "Here is some text\n```plain\nHere is some content\n``` some content here"
  end

  pending "unencodes HTML entities inside the code markers" do
    processor.process('X &amp; Y [plain]Here is an &quot;entity&quot;[/plain]').should == "X &amp; Y\n```plain\nHere is an 'entity'\n```"
  end

  describe "converts the language marker" do
    ["plain", "groovy", "html", "java", "python", "scala", "xml", "coldfusion", "js", "plain", "text", "code", "CODE"].each do |language|
      it "#{language}" do
        processor.process("[#{language}]Here is some content\n[/#{language}]").should == "```#{language.downcase}\nHere is some content\n```"
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
            processor.process("[#{marker} #{supported_attribute}=\"java\"]public class Foo{}[/#{marker}]").should == "```java\npublic class Foo{}\n```"
          end

          it "'#{supported_attribute}' and converts it to lower case markdown language syntax" do
            processor.process("[#{marker} #{supported_attribute}=\"JAVA\"]public class Foo{}[/#{marker}]").should == "```java\npublic class Foo{}\n```"
          end

          it "'#{supported_attribute}' and converts it ignoring additional attributes" do
            processor.process("[#{marker} #{supported_attribute}=\"java\" other_attribute=\"other\"]public class Foo{}[/#{marker}]").should == "```java\npublic class Foo{}\n```"
          end

        end
      end
    end
  end

  it "converts markers with unsupported attributes to markdown syntax and ignores the attributes" do
    processor.process('[groovy highlight="10,15"]public class Foo{}[/groovy]').should == "```groovy\npublic class Foo{}\n```"
    processor.process('[source light="true"]public class Foo{}[/source]').should == "```source\npublic class Foo{}\n```"
  end

end
