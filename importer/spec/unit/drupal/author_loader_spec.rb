require "drupal/author_loader.rb"
require "rspec"
require 'pg'

describe Drupal::AuthorLoader do

  let(:siteapi) {double('siteapi')}
  subject { Drupal::AuthorLoader.new(siteapi) }

  context "load" do
    it "creates a post with the correct processed content" do
      siteapi.should_receive('fetch_team_members').and_return(double(:parsed_response => {"John Doe" => "jdoe"}))

      subject.load.should == {"John Doe" => "jdoe"}
    end

  end

end