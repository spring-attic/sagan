## `Sagan` – a Spring reference application

Welcome! The code in this repository is deployed and running right now at <http://spring.io> – if you're not already
familiar with Spring or the spring.io site, you may want to take a look around there now for a bit of context.
The [blog][], the collection of [guides][], and most everything else you'll see is implemented right here in this app.

In addition to the practical purpose of powering Spring's home on the web, this project is also designed to serve
as a *reference application* for Spring – a resource that developers can use to see how the [Spring team][team] have
used Spring to implement a web app with a few interesting requirements. For that reason, you'll find documentation
everywhere you go: Javadoc in the code itself, README files in many directories as you explore the codebase, and on the
[project wiki][wiki] as well.

### Sagan? As in *Carl Sagan?*

Yeah, [that guy][sagan]. He was a pretty inspiring [explainer of things][cosmos], and we named this project after him
with the goal of explaining what modern-day Spring has to offer in the context of building a real-world web app. We
hope you enjoy and get value out of it, and that in some small way the project might live up to its namesake.

## Quick start – build and run the app locally

### 1. Install JDK 8

The Sagan application is implemented using the latest Java SE 8 language features, and this means that you'll need
to [install OpenJDK 8](https://jdk8.java.net/download.html), build 108 or greater.

Once installed, verify that JDK 8 binaries are properly on your PATH:

    $ java -version
    java version "1.8.0-ea"
    Java(TM) SE Runtime Environment (build 1.8.0-ea-b108)
    Java HotSpot(TM) 64-Bit Server VM (build 25.0-b50, mixed mode)

... and *that's it!* No app servers to install, no build systems to download. Plain old Java is all you need.

### 2. Grab the code

The recommended approach is to [fork and clone][fork] this repository using Git – this will allow you maximum freedom
to make changes, commit and push them to your own repository, share with others, and issue pull requests.

You can also check out the repository using Subversion if that's more convenient, or you can simply download a zip file
of the repository. All of these options are available from the clone / download widget to your right.

### 3. Build and run

The Sagan app is built using [Spring Boot][boot], which makes running any application as simple as executing a jar file.

First, assemble the JAR file using the built-in Gradle wrapper script:

    $ ./gradlew :sagan-site:build -x integTest

> **NOTE**: the `-x integTest` argument above skips longer-running integration tests in the interest of time.

Then run the app with `java -jar`:

    $ java -jar sagan-site/build/libs/sagan-site.jar

A few seconds later you'll see a log entry to the effect of

    sagan.app.site.ApplicationConfiguration  : Started ApplicationConfiguration in 8.455 seconds

And that means you can now browse the site at <http://localhost:8080>. Enjoy!

### Alternative, even faster ways to build and run

While it's important to understand the simple process of building and executing a jar as detailed above, an even more
convenient approach is to use the `run` Gradle task:

    $ ./gradlew :sagan-site:run

And since the application is ultimately bootstrapped via a standard Java [`main` method][main],  you can also run the
app directly from within your IDE. Follow the links in the section below to instructions for the IDE of your choice.

## Next steps

What's next is entirely up to you. Perhaps you'd simply to browse code and read docs, or perhaps you'd like to see
what it takes to [deploy the app][deploy] to Heroku or Cloud Foundry. Take a look at the [wiki][] where you'll find
plenty of
docs covering everything from getting set up in Eclipse and IntelliJ IDEA to how we manage deployments and
troubleshoot issues in production. And keep an eye out for the README docs that exist throughout the application.
Their purpose is to provide context for what's in each major different directory.

If you have any feedback or questions along the way, don't hesitate [add an issue][issues]. Thanks!

----

# About the contents of this directory


[sagan]: http://en.wikipedia.org/wiki/Carl_Sagan
[cosmos]: http://en.wikipedia.org/wiki/Cosmos:_A_Personal_Voyage
[blog]: http://spring.io/blog
[guides]: http://spring.io/guides
[team]: http://spring.io/team
[fork]: https://help.github.com/articles/fork-a-repo
[wiki]: https://github.com/cbeams/spring.io/wiki
[issues]: https://github.com/spring-io/spring.io/issues
[deploy]: https://github.com/cbeams/spring.io/wiki/TODO
[boot]: http://spring.io/spring-boot
[main]: sagan-site/src/main/java/sagan/app/site/ApplicationConfiguration.java#L62-L64
