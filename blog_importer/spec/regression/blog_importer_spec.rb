require "blog_importer.rb"
require "rspec"
require 'pg'
require 'wordpress_markup_processor'

describe "BlogImporter Regression" do

  let(:siteapi) { double('siteapi').as_null_object }
  let(:wp_processor) { WordpressMarkupProcessor.new }
  let(:importer) { BlogImporter.new(xml_filename, siteapi, wp_processor) }

  context "After importing test xml file" do
    let(:xml_filename) { "./spec/fixtures/test_blog_export.xml" }

    expected_content = <<-INPUT
With the release of Spring 1.1 ...

```java

public class Foo{}
```



```code
Example Message
```


One thing to note ...


```xml
<filter-mapping>
  <dispatcher>"ERROR"</dispatcher>
  <dispatcher>REQUEST&TEST</dispatcher>
</filter-mapping>
```



```xml

<bean id="listenerContainer"
	class="org.springframework.jms.listener.DefaultMessageListenerContainer">
	<property name="concurrentConsumers" value="5" />
</bean>
```


So, there you have it!
INPUT


    it "creates a post with the correct processed content" do
      expected_post = {
          title: 'Spring 2.0\'s JMS Improvements',
          content: expected_content,
          category: 'ENGINEERING',
          publishAt: '2006-04-09 20:22',
          createdAt: '2006-04-09 20:22',
          authorMemberId: 'sample',
      }
      siteapi.should_receive('save_blog_post').with(expected_post)
      importer.import(StringIO.new)
    end

  end

end