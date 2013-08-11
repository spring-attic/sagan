The following commits have been pushed to the shared gh-pages repository at
#{[push][repository][url]}:

#{[commits]}

Please update your Spring project repository's gh-pages branch by issuing the following commands:


If you need to do a fresh checkout
----------------------------------
    git clone git@github.com:spring-projects/#{[project].id}.git
    cd #{[project].id}


If you do not yet have the common gh-pages project as an upstream
----------------------------------
    git remote add gh-pages-upstream git@github.com:spring-projects/gh-pages.git


In any case
----------------------------------
    git checkout gh-pages
    git pull --no-ff gh-pages-upstream gh-pages


Analyze changes
----------------------------------
 - Work out merge conflicts, if any
 - Commit changes as necessary


Push
-----------------------------------
    git push origin gh-pages