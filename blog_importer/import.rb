require './lib/blog_importer'

site = SiteApi.new "localhost:8080"
# site = SiteApi.new "sagan.cfapps.io"

importer = BlogImporter.new "full_blog_export.xml", site
importer.import