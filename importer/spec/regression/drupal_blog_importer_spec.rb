require "drupal/blog_importer.rb"
require "rspec"
require 'pg'

describe "import blog posts from drupal xml file" do

  let(:authors) do
    {"Josh Long" => 'jlong'}
  end
  let(:siteapi) { double('siteapi').as_null_object }
  let(:importer) { Drupal::BlogImporter.new(xml_filename, siteapi, authors) }

  context "After importing test xml file" do
    let(:xml_filename) { "./spec/fixtures/drupal_test_export.xml" }

    it "creates a post with the correct processed content" do
      expected_content = <<-INPUT
<p>Welcome to another installment of <em>This Week in Spring</em>! We&#39;ve got a lot to cover this week, as usual, so let&#39;s get to it.</p>
<ol>
<li>Something exciting here!</li>
</ol>
      INPUT

      expected_post = {
          title: 'This Week In Spring - April 30th, 2013',
          content: expected_content,
          category: 'NEWS',
          publishAt: '2013-04-30 21:30',
          createdAt: '2013-04-30 21:30',
          authorMemberId: 'jlong',
      }
      siteapi.should_receive('save_blog_post').with(expected_post)
      importer.import(StringIO.new)
    end

  end

end