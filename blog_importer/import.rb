require './lib/export_cleaner'
require './lib/wordpress_markup_processor'
require './lib/blog_importer'
require './lib/site_api'

site = SiteApi.new "localhost:8080"
# site = SiteApi.new "sagan.cfapps.io"

import_filename = ARGV[0]

if import_filename.nil?
  puts "Usage: 'ruby import.rb import_filename.xml'"
  exit(1)
end

ExportCleaner.new.clean(import_filename, "clean_blog_export.xml")
importer = BlogImporter.new "clean_blog_export.xml", site, WordpressMarkupProcessor.new
importer.import

File.delete("clean_blog_export.xml")