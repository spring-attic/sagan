require "wordpress/blog_importer.rb"
require "wordpress/export_cleaner"
require "rspec"
require 'pg'

xml_import_filename = "full_blog_export.xml"
xml_test_filename = "clean_blog_export.xml"

describe "Full Blog Importer", if: File.exists?(xml_import_filename) do

  let(:siteapi) { double('siteapi').as_null_object }
  let(:wp_processor) { double('wp_processor').as_null_object }
  let(:importer) { Wordpress::BlogImporter.new(xml_test_filename, siteapi, wp_processor) }

  before(:all) do
    Wordpress::ExportCleaner.new.clean(xml_import_filename, xml_test_filename)
  end

  after(:all) do
    File.delete(xml_test_filename)
  end

  context "After importing full xml file" do

    it "creates new memberProfiles for an author" do
      siteapi.should_receive('save_member_profile').exactly(86).times
      importer.import(StringIO.new)
    end

    it "creates a post for every published post in the import file" do
      siteapi.should_receive('save_blog_post').exactly(525).times
      importer.import(StringIO.new)
    end
  end
end