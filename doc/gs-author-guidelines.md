## Use conventional style for git commits

See <https://github.com/SpringSource/spring-framework/blob/master/CONTRIBUTING.md#format-commit-messages>

## Common sections in all GSGs

- What you'll build

- What you'll need

- [macro:how-to-complete-this-guide]

- Set up the project

    - Create the directory structure

    - Create a Maven POM

- Steps A .. Z

- Summary

## do not mention cloning or downloading the repository in your guide.

Just include the [macro:how-to-complete-this-guide](https://github.com/springframework-meta/springframework.org/blob/master/doc/gs-macros.md) macro after the "What you'll need" section

## copy and paste your way to working code
At every step in the guide, the use should have code that compiles. In the end, the user should have code that runs.

## demonstrate what 'working code' means
Always end the guide with a final step that exercises the working code. For example, a guide that stands up a RESTful web service should exercise that service using 

## always create 'scratch' and 'initial' anchors

## naming

use `initial` and `complete` for directory names

use group id `org.springframework`

in the `initial/pom.xml`, use `<artifactId>gs-${name}</artifactId>`, e.g. `gs-rest-service`

in the `complete/pom.xml`, use `<artifactId>gs-${name}-complete</artifactId>, e.g. `gs-rest-service-complete`

## versioning

use 1.0-SNAPSHOT for all versions

## use a common .gitignore

can probably create this from a gs-template project after all...
for now, use the .gitignore in gs-rest-service

## use consistent versioning in links

3.2.x vs current, etc

## whitespace

- spaces, not tabs in all files
- 4-space indent in code

## Headings

### Title

Use the form

    Getting Started: [task gerund]

Examples of task [gerunds](http://en.wikipedia.org/wiki/Gerund):

 - _Building RESTful Web Services_
 - ...
 - ...


Capitalize the first letter of all major words in titles


### Section headings

Use imperative statements, e.g.:

 - Set up the project
 - Create a configuration class
 - Run the service

Use sentence-style capitalization, as seen above.

## Links

For fully-qualified inline links, use the short markdown form

    You'll need [JDK 7][jdk7] or better.
    ... rest of document ...
    [jdk7]: http://docs.oracle.com/javase/7/docs/webnotes/install/index.html


## Use macros for common content

See available macros at 

https://github.com/springframework-meta/springframework.org/blob/master/doc/gs-macros.md

And see their usage at

https://github.com/springframework-meta/gs-rest-service#readme


## set up a build plan at http://drone.io

e.g. https://drone.io/github.com/springframework-meta/gs-rest-service

be sure to set up the zapier integration to publish failed build reports to hipchat. This means simply sending email to sagan-ci.Xv6i@zapiermail.com from the settings > notification screen in your build. See https://drone.io/github.com/springframework-meta/gs-rest-service/admin/notifications for an example.


## set up hipchat webhook for your repository

----

## misc

- use 'hello' as package.
- single blank line between `package` declaration and `import` declarations
- single blank line between `import` declarations and class declaration.


- write a test script for the `complete` version of the code


## 4 spaces, not tabs (Java, XML, and all other file types)

- allows consistent, precise display of content
- no surprises when copying and pasting (i.e. markdown renders tabs as spaces, users pastes spaces, but `complete` version has tabs, so when user diffs to check their work they get whitespace differences, etc)


## leave no newline between final closing braces

```java
public class Foo {

    public static void main(String... args) {
        // ...
    }
}
```


## always add github project description for GSG repos


## add .gitignore file to any empty directories under 'initial'


## use reference-style links as a rule

    a sentence with [an embedded link][linkid]
    ...
    [linkid]: http://abc.com/xyz

http://daringfireball.net/projects/markdown/syntax


## use markdown blockquote syntax for indicating note/tip/warning/info sections

e.g.:

> **Tip:** This is a tip, and should be colored appropriately (maybe green), and given a border, etc.

Precedent: http://developer.android.com/sdk/installing/bundle.html
