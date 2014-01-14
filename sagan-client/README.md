# Sagan client module

This module holds all static web resources that make the client part of the site:
* CSS
* JavaScript
* images and fonts

Right now this module is using several tools for its own build system:
* [node.js and npm](http://nodejs.org)
* [Bower](http://bower.io)
* [Grunt](http://gruntjs.com)

## Build requirements

You'll need a 0.10.x version of [node.js](http://nodejs.org) installed on your system.
Usage of the [Node Version Manager (nvm)](https://github.com/creationix/nvm) is perfectly fine.

## Making changes in sagan-client

Right now (and until this part of the build is streamlined), static resources are generated and checked in
the `src/main/resources/static` folder in the sagan-site module. Of course, those resources will be deleted once
the sagan-client build is more transparent to the developer.

Here is the detailed If you want to make changes in the sagan-client module and make them available in sagan-site.

Open a terminal and run the sagan-site module as usual.

```
$ gradle :sagan-site:run
```

In another terminal, install all tools and dependencies (this must be done only once):

```
$ cd sagan-client
$ npm install
```

Now you can build resources and copy them in the sagan-site build directory; those will be available at runtime:

```
$ npm run build
```

This command concatenate, optimize and copy all required files.
Check out the `optimize` grunt task in `Gruntfile.js`.

## Dump static resources in sagan-site module

Until a more convenient solution is found, static resouces should be dumped into the sagan-site resources directory
before being pushed to production:

```
$ cd sagan-client
$ npm run dump
```

Check out the `dump` grunt task in `Gruntfile.js`.

References:
* [Addy Osmani - Checking in front end dependencies](http://addyosmani.com/blog/checking-in-front-end-dependencies/)