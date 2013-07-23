require "blog_importer.rb"
require "rspec"
require 'pg'

describe BlogImporter do

  let(:siteapi) { double('siteapi').as_null_object }
  let(:wp_processor) { double('wp_processor').as_null_object }
  let(:importer) { BlogImporter.new(xml_filename, siteapi, wp_processor) }

  context "After importing test xml file" do
    let(:xml_filename) { "./spec/fixtures/test_blog_export.xml" }

    it "creates new memberProfiles for an author" do
      siteapi.should_receive('save_member_profile').exactly(3).times
      importer.import
    end

    it "creates memberProfiles with the correct info" do
      expected_author = {
          memberId: 'sample',
          githubUsername: 'sample',
          gravatarEmail: 'sample@springsource.com',
          name: 'Mr Sample'
      }
      siteapi.should_receive('save_member_profile').with(expected_author)
      importer.import
    end

    it "creates a post for every published post in the import file" do
      siteapi.should_receive('save_blog_post').exactly(2).times
      importer.import
    end

    it "creates a post with the correct info" do
      wp_processor.stub(:process).and_return("Interceptor Combining is out of this world!")
      expected_post = {
          title: 'Another Reason to Love Spring 2.0: Interceptor Combining',
          content: 'Interceptor Combining is out of this world!',
          category: 'ENGINEERING',
          publishAt: '2006-04-09 20:41:19',
          createdAt: '2006-04-09 20:41:19',
          authorMemberId: 'sample',
      }
      siteapi.should_receive('save_blog_post').with(expected_post)
      importer.import
    end

    it "processes wordpress content in blog posts" do
      wp_processor.should_receive(:process).ordered.with("[code lang=\"java\"]")
      wp_processor.should_receive(:process).ordered.with("package jmsexample;")
      importer.import
    end

  end

end