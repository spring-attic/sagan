require './lib/wordpress/export_cleaner'
require './lib/wordpress/markup_processor'
require './lib/wordpress/blog_importer'
require './lib/site_api'

site = SiteApi.new "localhost:8080"
#site = SiteApi.new "sagan.cfapps.io"

import_filename = ARGV[0]

if import_filename.nil?
  puts "Usage: 'ruby import_from_wordpress.rb import_filename.xml'"
  exit(1)
end

Tempfile.open('clean_blog_export') do |f|
  Wordpress::ExportCleaner.new.clean(import_filename, f.path)
  importer = Wordpress::BlogImporter.new(f.path, site, Wordpress::MarkupProcessor.new)

  File.open("mappings.txt", "w+") do |f|
    importer.import(f)
  end
end
