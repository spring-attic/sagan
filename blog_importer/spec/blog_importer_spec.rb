require "blog_importer.rb"
require "rspec"
require 'pg'
#require 'pry'

describe BlogImporter do


  test_files = [{ filename: "test_blog_export.xml", expected_post_count: 2 } ]
  test_files << { filename: "full_blog_export.xml", expected_post_count: 525 } if File.exists?("full_blog_export.xml")

  test_files.each do |test_context|

    context "After importing #{test_context[:filename]}" do

      before(:all) do
        importer = BlogImporter.new(test_context[:filename])
        importer.import!
      end

      let(:expected_post_count) do
        test_context[:expected_post_count]
      end

      let(:db) do
        PG.connect(dbname: 'blog_import')
      end

      it "truncates the table before performing an import" do
        importer = BlogImporter.new(test_context[:filename])
        importer.import! # Import a second time, after the before block
        db.exec("SELECT count(*) FROM post") do |result|
          result.getvalue(0, 0).to_i.should == expected_post_count
        end
      end

      it "creates a post for every published post in the import file" do
        db.exec("SELECT count(*) FROM post") do |result|
          result.getvalue(0, 0).to_i.should == expected_post_count
        end
      end

      context "first post in the database" do
        subject { db.exec("SELECT * FROM post ORDER BY id ASC LIMIT 1")[0] }

        its(['title']) { should == "Spring 2.0's JMS Improvements" }
        its(['broadcast']) { should == "f" }
        its(['category']) { should == "ENGINEERING" }
        its(['createdat']) { should == "2006-04-09 20:22:43" }
        its(['draft']) { should == "f" }
        its(['publishat']) { should == "2006-04-09 20:22:43" }
        its(['rawcontent']) { should start_with("With the release of Spring 1.1 the Spring community was given") }
        its(['renderedcontent']) { should start_with("With the release of Spring 1.1 the Spring community was given") }
        its(['renderedsummary']) { should start_with("With the release of Spring 1.1 the Spring community was given") }
        its(['author_id']) { should == "1" }

        it "has a shortened summary" do
          subject['renderedsummary'].length.should <= 500
          # TODO end with word
        end

      end

    end

  end

end