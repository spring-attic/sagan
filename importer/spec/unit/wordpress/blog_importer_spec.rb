require "wordpress/blog_importer.rb"
require "rspec"
require 'pg'

describe Wordpress::BlogImporter do

  let(:siteapi) { double('siteapi').as_null_object }
  let(:wp_processor) { double('wp_processor').as_null_object }
  subject { Wordpress::BlogImporter.new(xml_filename, siteapi, wp_processor) }

  context "After importing test xml file" do
    let(:xml_filename) { "./spec/fixtures/test_blog_export.xml" }

    it "creates new memberProfiles for an author" do
      siteapi.should_receive('save_member_profile').exactly(3).times
      subject.import(StringIO.new)
    end

    it "creates memberProfiles with the correct info" do
      expected_author = {
          username: 'sample',
          gravatarEmail: 'sample@springsource.com',
          name: 'Mr Sample'
      }
      siteapi.should_receive('save_member_profile').with(expected_author).once
      subject.import(StringIO.new)
    end

    it 'sanitizes the username' do
      expected_author = hash_including(username: 'full_name')

      siteapi.should_receive(:save_member_profile).with(expected_author).once
      subject.import(StringIO.new)
    end

    it "creates a post for every published post in the import file" do
      siteapi.should_receive('save_blog_post').exactly(2).times
      subject.import(StringIO.new)
    end

    it "creates a post with the correct info" do
      wp_processor.stub(:process).and_return("Interceptor Combining is out of this world!")
      expected_post = {
          title: 'Another Reason to Love Spring 2.0: Interceptor Combining',
          content: 'Interceptor Combining is out of this world!',
          category: 'ENGINEERING',
          publishAt: '2006-04-09 20:41',
          createdAt: '2006-04-09 20:41',
          username: 'sample',
      }
      siteapi.should_receive('save_blog_post').with(expected_post).and_return(double(:code => 200, :headers => {}))
      subject.import(StringIO.new)
    end

    it "writes blog post url mappings to a file" do
      headers1 = {"Location" => "http://example.com/blog/1-a-post"}
      headers2 = {"Location" => "http://example.com/blog/2-another-post"}

      siteapi.should_receive('save_blog_post').ordered.and_return(double(:code => 200, :headers => headers1))
      siteapi.should_receive('save_blog_post').ordered.and_return(double(:code => 200, :headers => headers2))
      mappings = StringIO.new
      subject.import(mappings)
      expected_mappings = [
        {
          "old_url" => "http://blog.springsource.org/2006/04/09/spring-20s-jms-improvements/",
          "new_url" => "http://example.com/blog/1-a-post"
        },
        {
          "old_url" => "http://blog.springsource.org/2006/04/09/another-reason-to-love-spring-20-interceptor-combining/",
          "new_url" => "http://example.com/blog/2-another-post"
        }
      ]
      mappings.string.should == expected_mappings.to_yaml
    end

  end

end