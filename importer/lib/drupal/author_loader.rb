module Drupal
  class AuthorLoader
    def initialize(siteapi)
      @siteapi = siteapi
    end

    def load
      @siteapi.fetch_team_members.parsed_response
    end
  end
end