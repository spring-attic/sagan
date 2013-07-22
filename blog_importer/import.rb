require './lib/export_cleaner'
require './lib/wordpress_markup_processor'
require './lib/blog_importer'
require './lib/site_api'

site = SiteApi.new "localhost:8080"
# site = SiteApi.new "sagan.cfapps.io"

ExportCleaner.new.clean("full_dirty_blog_export.xml", "clean_blog_export.xml")
importer = BlogImporter.new "clean_blog_export.xml", site, WordpressMarkupProcessor.new
importer.import

File.delete("clean_blog_export.xml")