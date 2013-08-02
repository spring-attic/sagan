require './lib/drupal/blog_importer'
require './lib/drupal/author_loader'
require './lib/site_api'

site = SiteApi.new "localhost:8080"
#site = SiteApi.new "sagan.cfapps.io"

import_filename = ARGV[0]

if import_filename.nil?
  puts "Usage: 'ruby import_from_drupal.rb import_filename.xml'"
  exit(1)
end

authors = Drupal::AuthorLoader.new(site).load
importer = Drupal::BlogImporter.new(import_filename, site, authors)

File.open("mappings.txt", "w+") do |f|
  importer.import(f)
end
