require "wordpress/export_cleaner.rb"
require "rspec"
require "nokogiri"

describe Wordpress::ExportCleaner do

  after { File.delete("clean_export.xml") }

  it "HTML encodes CData declarations inside CData" do
    subject.clean("spec/fixtures/embedded_cdata.xml", "clean_export.xml")

    File.open "clean_export.xml" do |clean_file|
      xml_doc = Nokogiri::XML(open(clean_file))
      xml_doc.errors.should == []
    end

  end

  it "replaces ^C and ^K characters with a space" do
    subject.clean("spec/fixtures/invalid_control_characters.xml", "clean_export.xml")

    File.open "clean_export.xml" do |clean_file|
      xml_doc = Nokogiri::XML(open(clean_file))
      xml_doc.errors.should == []
    end

  end

  it "retains unicode characters" do
    subject.clean('spec/fixtures/unicode_text.xml', 'clean_export.xml')

    FileUtils.compare_file('spec/fixtures/unicode_text.xml', 'clean_export.xml').should be_true
  end

end
