require 'pg'
require 'nokogiri'

class BlogImporter
  def initialize(filename)
    @filename = filename
  end

  def import!
    import(true)
  end

  def import(force=false)
    return false unless force || user_happy_to_continue

    truncate_post_table
    import_posts
  end

  def import_posts
    puts "Importing #{@filename} "
    post_elements = xml_doc.xpath("//item[wp:status/text()='publish'][wp:post_type/text()='post']")
    post_elements.each_with_index do |element, i|
      print_progress
      title = element.xpath('title').text
      post_date = element.xpath('wp:post_date_gmt').text
      content = element.xpath('content:encoded').text

      query = <<-EOS
        INSERT INTO post (id, broadcast, category, createdat, draft, publishat, rawcontent, renderedcontent, renderedsummary, title, author_id)
        VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11)
      EOS

      values = [i, false, 'ENGINEERING', post_date, false, post_date,
                content, content, content[0...500], title, 1]
      conn.exec(query, values)
    end
    true
  end

  def print_progress
    print "."
    $stdout.flush
  end

  def user_happy_to_continue
    puts "This will remove all existing posts from the database. Enter 'yes' to continue"
    gets.chomp == 'yes'
  end

  def truncate_post_table
    conn.exec("TRUNCATE post")
  end

  def xml_doc
    @xml_doc ||= File.open(@filename) do |f|
      Nokogiri::XML(open(f))
    end
  end

  def blog_file_name
    File.exists?(FULL_BLOG_FILE) ? FULL_BLOG_FILE : TEST_BLOG_FILE
  end

  def conn
    @conn ||= PG.connect(dbname: 'blog_import')

    # The cloudfoundry development db
    #@conn ||= PG::Connection.new({"dbname"=>"zbgubjyl", "host"=>"babar.elephantsql.com", "port"=>5432, "user"=>"zbgubjyl", "password"=>"BOHcUgFDTIPJ8KxCX9tT3w0sZct5WyCR"})

  end
end