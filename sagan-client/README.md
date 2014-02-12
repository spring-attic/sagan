# Sagan client module

This module holds all static web resources that make the client part of the site:
* CSS
* JavaScript
* images and fonts

Right now this module is using several tools for its own build system:
* [node.js and npm](http://nodejs.org)
* [Cram](https://github.com/cujojs/cram)
* [Bower](http://bower.io)
* [Gulp](http://gulpjs.com)

## Build requirements

You'll need a 0.10.x version of [node.js](http://nodejs.org) installed on your system.
Usage of the [Node Version Manager (nvm)](https://github.com/creationix/nvm) is perfectly fine.

## Making changes in sagan-client

Right now, you have to run the sagan-site application and copy files from `src/**` in the gradle build directory
during development cycles. Of course, this is far from an ideal development experience.
In the near future, sources in sagan-client/src will be served directly sagan-site when running in dev mode.

If you want to make changes in the sagan-client module and make them available right away at dev time,
while running sagan-site on your computer, please follow those steps.

Open a terminal and run the sagan-site module as usual.

```
$ gradle :sagan-site:run
```

In another terminal, install all tools and dependencies (this last step must be done only once, or when dependencies
changed):

```
$ cd sagan-client
$ npm install
```

Instead of copying yourself resources every time you change a file, you can launch a watch process that will do that
for you:

```
$ npm run watch
```

One should always build the complete application and test it before pushing changes on the repo (see next chapter).

## Building the application

Running:

```
$ gradle :sagan-client:build
```

will do all the necessary steps:

- run the JavaScript build
- package those artifacts into a single sagan-client.jar that will be used as a dependency by sagan-site


## Details about the JavaScript build

If you want to know more about the JavaScript build, this chapter will help you; reading this is not needed.

### Node.js and npm

npm is the node package manager; it installs required dependencies in the `node_modules` directory.
Check the `package.json` file to read:

- all dependencies and their versions in `devDependencies`
- all available `scripts` that you can run with `npm run scriptname`

Note: we make extensive use of npm scripts so you don't have to install binaries globally on your system's PATH.
npm dynamically adds binaries listed in `node_modules/.bin` to its own PATH.

### Cram

[Cram](https://github.com/cujojs/cram) assembles JavaScript resources into bundles. Cram does not take care of the
optimizing phase.
Cram is run as a command-line tool in the scripts section of our `package.json`.

Note: we're currently running cram on the command-line, but we may integrate cram in our gulp build in the future, if
a `gulp-cram` gulp plugin is made available.

### Bower

Bower installs front-end dependencies like jquery or twitter bootstrap into`src/lib`.
That way, developers don't have to copy/paste/commit those resources manually.
All dependencies and their versions are listed in `bower.json`; we're also defining overrides in order to specify
which resources we want to retain when packaging for production (we certainly don't want to add hundreds of files
that have no use in our application).

Note: this use of overrides may be against the official guidelines, even though it's widely supported by the community.

### Gulp

Gulp is the build system we're using to make things work together.
We've defined all tasks in our `gulpfile.js` file.
In this build, we're using dependencies that were downloaded by npm and we're concatenating/optimizing/packaging
resources that are part of our application or downloaded by Bower.