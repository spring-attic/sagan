# Managing Getting Started Guides

- Use GitHub for Mac|Windows
- Point it at springframework-meta org
- Clone everything under one parent dir
- This is the easiest way to get all the repos and keep them up to date

- use `mr`, the multiple repository tool: https://github.com/joeyh/mr, or if you are using a Mac with homebrew, simply type `brew install mr`.
- cd to your springframework-meta root directory
```sh
for repo in $(find . -name .git); do
	cd $(dirname $repo); pwd; mr register; cd ..;
done;
```
- now the following will work:
```
mr status;
mr update;
```

## Regenerate guides when macros.md is updated

From time to time, the macros may get updated. It could something as simple as a single word being edited,
but it ripples out to every guide. This means they must all be regenerated (assuming CI hasn't been configured).

```sh
for repo in $(find . -name .git); do
	cd $(dirname $repo); pwd; cat README.ftl.md | fpp > README.md; cd ..;
done;
```

If you generate guides frequently, it may be useful to put an alias in your ~/.bash_profile like:

    alias f=`cat README.ftl.md | fpp > README.md`

Then the above command be shortened to:

```sh
for repo in $(find . -name .git); do
	cd $(dirname $repo); pwd; f; cd ..;
done;
```

Either way, you need to review your changes before committing. (With great power comes great responsibility)

    mr diff
    
Review each change. If something is out-of-whack, fix that specific repository. Then:

    mr commit -m "your commit message"

> **Note:** You may get authentication prompts for some of the repositories. Use the osxkeychain git helper to make so you only have to log in once:

> On May 10, 2013, at 9:00 PM, Justin Spahr-Summers (GitHub Staff) <support@github.com> wrote:

>> If you have the [osxkeychain](https://help.github.com/articles/set-up-git#password-caching) helper, logging in through GitHub > Preferences will automatically save your username and password into the keychain, and then git on the command line will be able to use those saved credentials instead of prompting you.
