How to run
====

From `$SPRING_PROJECT/importer`

Follow these steps:

1. install rvm (follow all of the rvm instructions!)

``` bash
\curl -L https://get.rvm.io | bash -s stable
```

2. use RVM to install ruby 1.9.3

``` bash
rvm install 1.9.3
```

Changing back into the directory allows RVM to do it's magic. Say yes to any prompts.

``` bash
cd ..
cd importer
```

3. bundle

``` bash
bundle install
```

4. run the script wordpress import

``` bash
bundle exec ruby import_from_wordpress.rb xml_file_that_has_all_data.xml
```

5. run the drupal import

``` bash
bundle exec ruby import_from_drupal.rb xml_file_that_has_all_data.xml
```

Caveats
---

* The drupal xml export sometimes has invisible characters which cause it to be malformed xml. Use an editor (such as vim) to find them. 
* The encoding is incorrect. Please change UTF-8 to ISO-8859-1.
