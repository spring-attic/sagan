require "site_api.rb"
require "rspec"

describe SiteApi do

  let(:username) { 'yada' }
  let(:post_title) { 'this is a test blog post' }

  it "saves a member profile" do
    api = SiteApi.new('localhost:8080')
    response = api.save_member_profile(username: username)

    response.code.should satisfy { |v| v == 200 || v == 201 }
  end

  it "saves a blog post" do
    api = SiteApi.new('localhost:8080')
    response = api.save_blog_post(title: post_title,
                       content: 'this is a blog post',
                       category: 'ENGINEERING',
                       publishAt: "2000-01-01 00:00",
                       createdAt: "1999-01-01 00:00",
                       authorUsername: username,)

    response.code.should satisfy { |v| v == 200 || v == 201 }
  end

  it "fetches team members" do
    api = SiteApi.new('localhost:8080')
    response = api.fetch_team_members
    response.code.should == 200
    puts response.body
  end

end