require 'httparty'

class SiteApi
  def initialize input_base_uri
    @base_uri = input_base_uri
  end

  def save_member_profile(options)
    HTTParty.post("http://#{@base_uri}/migration/profile", {body: options})
  end

  def save_blog_post(options)
    HTTParty.post("http://#{@base_uri}/migration/blogpost", {body: options})
  end
end