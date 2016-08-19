
_Wondering why something in Sagan is implemented the way that it is? Find something that doesn't work the way the [wiki](https://github.com/spring-io/sagan/wiki) says it should? Have an idea for an improvement? Want to fix or implement it yourself, even? You're in the right place!_


## Asking questions and reporting issues

Everything regarding Sagan is tracked in [GitHub Issues](https://github.com/spring-io/sagan/issues). Just create a new issue, and we'll discuss. It's a good idea to search first, though, to see if something similar has already been asked or reported.


## Pull requests

If you want to contribute code to the project, you'll need to be familiar with GitHub's notion of [pull requests](https://help.github.com/articles/using-pull-requests) (they're awesome). See also the section below on `hub`.

We also ask that you sign our [Contributor License Agreement](https://cla.pivotal.io/sign/spring). It's not too big a deal, just a form to fill out.


## Coding conventions

Our formatting and import organization profiles are checked into the project under the [style](https://github.com/spring-io/sagan/tree/master/style) directory. Check out the README there for instructions how to import them into Eclipse or IDEA.


## Use `hub` for pull requests

If you're not already familiar, GitHub's [`hub`](http://hub.github.com) utility is a simple, transparent layer on top of your existing `git` installation that adds amazingly useful tools specific to working with GitHub. Install it post-haste! It'll allow you to do super-powerful things like this:

    # nothing unusual here...
    git checkout my-feature-branch
    git push myfork my-feature-branch

    # but the following is only possible with hub--
    # it will submit the changes in my-feature-branch
    # by converting issue #123 into a pull request. Win.
    git pull-request -i 123

Besides being really convenient, the Sagan development team heavily uses this feature of `hub` to avoid creating extra issues. We'll create an issue, discuss it, and then at some point submit a pull request against it as above. This way, everything about that change is in one place, neat and tidy.


## Commit like a pro

Read Pro Git's section on [Commit Guidelines](http://www.git-scm.com/book/en/Distributed-Git-Contributing-to-a-Project#Commit-Guidelines), and then read it again. Understanding how to structure git commits and properly format comments will pay off for every Git project you contribute to in the future. Look through Sagan's commit history with `git log`, and do your best to follow suit.


## Expect discussion and rework

For anything other than really trivial fixes, there will probably be some discussion involved before your pull request is merged. If you're thinking about a significant change, it's a good idea to discuss it with the team first, before spending a lot of time putting together a pull request.


## Have fun!

Thanks for your interest and participation--we'll see you over in [Issues](https://github.com/spring-io/sagan/issues)!

