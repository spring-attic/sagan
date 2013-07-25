require './lib/export_cleaner'
require './lib/wordpress_markup_processor'
require './lib/blog_importer'
require './lib/site_api'

site = SiteApi.new "localhost:8080"
#site = SiteApi.new "sagan.cfapps.io"

import_filename = ARGV[0]

if import_filename.nil?
  puts "Usage: 'ruby import.rb import_filename.xml'"
  exit(1)
end

Tempfile.open('clean_blog_export') do |f|
  ExportCleaner.new.clean(import_filename, f.path)
  importer = BlogImporter.new(f.path, site, WordpressMarkupProcessor.new)

  File.open("mappings.txt", "w+") do |f|
    importer.import(f)
  end
end
