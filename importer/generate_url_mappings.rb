require './lib/redirect/importer'

import_filename = ARGV[0]

if import_filename.nil?
  puts "Usage: 'ruby generate_url_mappings.rb import.csv'"
  exit(1)
end

importer = Redirect::Importer.new({
    "mainSiteBaseUrl" => "http://springframework.io",
    "projectsBaseUrl" => "http://projects.springframework.io",
    "atticBaseUrl" => "http://attic.springframework.io"
                                  })

puts importer.import(File.new(import_filename))
