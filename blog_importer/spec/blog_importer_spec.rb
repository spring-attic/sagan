require "blog_importer.rb"
require "rspec"
require 'pg'
require 'httparty'

describe SiteApi do

  it "saves a member profile" do
    db = PG.connect(dbname: 'blog_import')
    db.exec("DELETE FROM memberprofile WHERE memberid = 'yada'")
    db.exec("SELECT count(*) FROM memberprofile WHERE memberid = 'yada'") do |result|
      result.getvalue(0, 0).to_i.should == 0
    end

    api = SiteApi.new('localhost:8080')
    api.save_member_profile(memberId: 'yada')

    db.exec("SELECT count(*) FROM memberprofile WHERE memberid = 'yada'") do |result|
      result.getvalue(0, 0).to_i.should == 1
    end
  end

end

describe BlogImporter do

  test_files = [{ filename: "test_blog_export.xml",
                  expected_post_count: 2 ,
                  expected_author_count: 2,
                  expected_author: {
                      memberId: 'sample',
                      githubUsername: 'sample',
                      gravatarEmail: 'sample@springsource.com',
                      name: 'Mr Sample'
                  }
                } ]

  if File.exists?("full_blog_export.xml")
    test_files << { filename: "full_blog_export.xml",
                    expected_post_count: 525,
                    expected_author_count: 86,
                    expected_author: {
                        memberId: 'benh',
                        githubUsername: 'benh',
                        gravatarEmail: 'bhale@vmware.com',
                        name: 'Ben Hale'
                    }
                  }
  end

  test_files.each do |test_context|


    context "After importing #{test_context[:filename]}" do

      let(:double_siteapi) { double('siteapi').as_null_object }

      let(:db) { PG.connect(dbname: 'blog_import') }

      let(:importer) { BlogImporter.new(test_context[:filename], double_siteapi) }

      let(:expected_post_count) { test_context[:expected_post_count] }
      let(:expected_author_count) { test_context[:expected_author_count] }
      let(:expected_author) { test_context[:expected_author] }


      it "creates new memberProfiles for an author" do
        double_siteapi.should_receive('save_member_profile').exactly(expected_author_count).times
        importer.import!
      end

      it "creates memberProfiles with the correct info" do
        double_siteapi.should_receive('save_member_profile').with(expected_author)
        importer.import!
      end

      it "truncates the post table before performing an import" do
        importer.import!
        importer.import!
        db.exec("SELECT count(*) FROM post") do |result|
          result.getvalue(0, 0).to_i.should == expected_post_count
        end
      end

      it "creates a post for every published post in the import file" do
        importer.import!
        db.exec("SELECT count(*) FROM post") do |result|
          result.getvalue(0, 0).to_i.should == expected_post_count
        end
      end

      context "first post in the database" do
        subject do
          importer.import!
          db.exec("SELECT * FROM post ORDER BY id ASC LIMIT 1")[0]
        end

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