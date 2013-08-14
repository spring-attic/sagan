require "rspec"
require 'redirect/importer'

describe Redirect::Importer do
  subject do
    Redirect::Importer.new({
        "mainSiteBaseUrl" => "http://springframework.io",
        "anotherUrl" => "http://foo.com"
                           })
  end

  it "filters out rows where redirect column != 'yes'" do
    enumerator = [
        {"Redirect? default=no" => "no", "URL" => "http://example.com/page1"},
        {"Redirect? default=no" => "yes", "URL" => "http://example.com/page2"}
    ].each

    subject.filter_non_redirects(enumerator).to_a.should == [
        {"Redirect? default=no" => "yes", "URL" => "http://example.com/page2"}
    ]
  end

  it "transforms urls with variable substitutions" do
    enumerator = [
        {"Redirect Target URI" => "http://example.com/page1"},
        {"Redirect Target URI" => "{mainSiteBaseUrl}/page2"},
        {"Redirect Target URI" => "{anotherUrl}/page3"}
    ].each

    subject.substitute_url_variables(enumerator).to_a.should == [
        {"Redirect Target URI" => "http://example.com/page1"},
        {"Redirect Target URI" => "http://springframework.io/page2"},
        {"Redirect Target URI" => "http://foo.com/page3"}
    ]
  end

  it "filters out rows where the source or target url does not start with 'http://'" do
    enumerator = [
        {"URL" => "http://sourcehost/page", "Redirect Target URI" => "example.com/page1"},
        {"URL" => "http://sourcehost/page", "Redirect Target URI" => "http://example.com/page2"},
        {"URL" => "http://sourcehost/page", "Redirect Target URI" => nil},
        {"URL" => "sourcehost/page", "Redirect Target URI" => "http://example.com/page123"},
        {"URL" => nil, "Redirect Target URI" => "http://example.com/page456"}
    ].each

    subject.filter_invalid_redirect_uris(enumerator).to_a.should == [
        {"URL" => "http://sourcehost/page", "Redirect Target URI" => "http://example.com/page2"}
    ]

  end

  it "transforms redirect rows into XML" do
    enumerator = [
        {"URL" => "http://oldsite.com/page1", "Redirect Target URI" => "http://newsite.com/page1"},
    ].each

    subject.transform_into_xml_rules(enumerator).to_a.should == [
<<-XML
<rule>
    <condition name="host">oldsite.com</condition>
    <from>^/page1$</from>
    <to type="permanent-redirect" last="true">http://newsite.com/page1</to>
</rule>
XML
    ]
  end

  it "returns an xml snippet with all the rewrite rules from a CSV file" do
    csv = <<-CSV
URL,Category,Migrate/Redesign/Drop,Redirect? default=no,Notes,Redirect Target URI,Owner,
http://www.springsource.org/page1,homepage,redesign,yes,notes,http://www.spring.io/page1,Chris,
http://www.springsource.org/page2,homepage,redesign,at DNS level,notes,whatever,Chris,
http://www.springsource.org/page3,homepage,redesign,yes,notes,{mainSiteBaseUrl}/page3,Chris,
CSV
    result = subject.import(StringIO.new(csv))
    result.should == <<-XML
<rule>
    <condition name="host">www.springsource.org</condition>
    <from>^/page1$</from>
    <to type="permanent-redirect" last="true">http://www.spring.io/page1</to>
</rule>
<rule>
    <condition name="host">www.springsource.org</condition>
    <from>^/page3$</from>
    <to type="permanent-redirect" last="true">http://springframework.io/page3</to>
</rule>
XML
  end

end