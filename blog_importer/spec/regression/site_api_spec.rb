require "site_api.rb"
require "rspec"
require 'pg'

describe SiteApi do

  member_id = 'yada'
  post_title = 'this is a test blog post'
  db = PG.connect(dbname: 'blog_import')

  before do
    db.exec("DELETE FROM post WHERE title = '#{post_title}'")
    db.exec("DELETE FROM memberprofile WHERE memberid = '#{member_id}'")
  end

  after do
    db.exec("DELETE FROM post WHERE title = '#{post_title}'")
    db.exec("DELETE FROM memberprofile WHERE memberid = '#{member_id}'")
  end

  it "saves a member profile" do
    api = SiteApi.new('localhost:8080')
    api.save_member_profile(memberId: member_id)

    db.exec("SELECT count(*) FROM memberprofile WHERE memberid = '#{member_id}'") do |result|
      result.getvalue(0, 0).to_i.should == 1
    end
  end

  it "saves a blog post" do
    db.exec("INSERT INTO memberprofile (memberid, id, hidden) VALUES ('#{member_id}',130303, 't')")

    api = SiteApi.new('localhost:8080')
    api.save_blog_post(title: post_title,
                       content: 'this is a blog post',
                       category: 'ENGINEERING',
                       publishAt: "2000-01-01 00:00",
                       createdAt: "1999-01-01 00:00",
                       authorMemberId: member_id,)

    db.exec("SELECT count(*) FROM post WHERE title = '#{post_title}'") do |result|
      result.getvalue(0, 0).to_i.should == 1
    end
  end

end