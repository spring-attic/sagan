# Sagan client module

This module holds all web resources that make the client application of spring.io:

* JavaScript modules
* CSS styles
* images and fonts
* front-end dependencies

This module is using several tools for its own build system:

* [node.js and npm](http://nodejs.org)
* [Webpack](https://webpack.js.org/)

## Build requirements

Node.js is brought by the Gradle build itself, so you don't need it to build the project.
But if you want to work on the `sagan-client` module, installing a recent version of [node.js](http://nodejs.org)
is a good idea. Usage of the [Node Version Manager (nvm)](https://github.com/nvm-sh/nvm) is perfectly fine.

## Making changes in sagan-client

When running the application with the `SiteApplication` class in your IDE, resources in sagan-client are served
directly from the sagan-client module, so you can develop against the unoptimized version of the client.

For this to work, your IDE should be configured to consider the `sagan-site` module as its working directory. Check out
the [run the site locally](https://github.com/spring-io/sagan/wiki/Run-the-site-locally) section on the wiki.

## Details about the JavaScript build

If you want to know more about the JavaScript build, this chapter will help you; reading this is not required.

The JavaScript application can be built manually with (the build result is located in the `build/dist` folder):

```
$ npm run build
```

### Node.js and npm

npm is the node package manager; it installs required dependencies in the `node_modules` directory.
Check the `package.json` file to find:

- all dependencies and their versions in `devDependencies`
- all available `scripts` that you can run with `npm run scriptname`

Note: we make extensive use of npm scripts so you don't have to install binaries globally on your system's PATH.
npm dynamically adds binaries listed in `node_modules/.bin` to its own PATH.
