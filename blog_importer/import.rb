require './lib/export_cleaner'
require './lib/wordpress_markup_processor'
require './lib/blog_importer'

site = SiteApi.new "localhost:8080"
# site = SiteApi.new "sagan.cfapps.io"

ExportCleaner.new.clean("full_dirty_blog_export.xml", "full_clean_blog_export.xml")
WordpressMarkupProcessor.new.process("full_clean_blog_export.xml", "full_processed_clean_blog_export.xml")
importer = BlogImporter.new "full_processed_clean_blog_export.xml", site
importer.import

File.delete("full_clean_blog_export.xml")
File.delete("full_processed_clean_blog_export.xml")