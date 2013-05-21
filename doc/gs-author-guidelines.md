## Use conventional style for git commits

See <https://github.com/SpringSource/spring-framework/blob/master/CONTRIBUTING.md#format-commit-messages>.

## Common sections in all GSGs

- What you'll build

- What you'll need

- [macro:how-to-complete-this-guide]

- Set up the project

    - Create the directory structure

    - Create a Maven POM

- Steps A .. Z

- Summary

## Headings

### Title

Use the form

    Getting Started: [task gerund]

Examples of task [gerunds](http://en.wikipedia.org/wiki/Gerund):

 - _Building RESTful Web Services_
 - ...
 - ...


Capitalize the first letter of all major words in titles.


### Section headings

Use imperative statements, for example:

 - Set up the project
 - Create a configuration class
 - Run the service

Use sentence-style capitalization, as seen above.

## Use macros for common content

See available macros at 

https://github.com/springframework-meta/springframework.org/blob/master/doc/gs-macros.md

See their usage at

https://github.com/springframework-meta/gs-rest-service#readme

## Do not mention cloning or downloading the repository in your guide.

Just include the [macro:how-to-complete-this-guide](https://github.com/springframework-meta/springframework.org/blob/master/doc/gs-macros.md) macro after the "What you'll need" section

## Copy and paste your way to working code
At every step in the guide, the use should have code that compiles. In the end, the user should have code that runs.

## Demonstrate what 'working code' means
Always end the guide with a final step that exercises the working code. For example, a guide that stands up a RESTful web service should exercise that service using. 

## Always create 'scratch' and 'initial' anchors

## Naming

Use `initial` and `complete` for directory names.

Use group id `org.springframework`.

In the `initial/pom.xml`, use `<artifactId>gs-${name}</artifactId>`; for example, `gs-rest-service`.

in the `complete/pom.xml`, use `<artifactId>gs-${name}-complete</artifactId>; for example, `gs-rest-service-complete`.

## versioning

Use 1.0-SNAPSHOT for all versions.

## Use a common .gitignore

You can probably create this from a gs-template project after all...
for now, use the .gitignore in gs-rest-service.

## Use consistent versioning in links

3.2.x vs current, etc


## For fully-qualified inline links, use the short markdown form

    You'll need [JDK 7][jdk7] or better.
    ... rest of document ...
    [jdk7]: http://docs.oracle.com/javase/7/docs/webnotes/install/index.html

## Use reference-style links as a rule

    a sentence with [an embedded link][linkid]
    ...
    [linkid]: http://abc.com/xyz

http://daringfireball.net/projects/markdown/syntax


## Wrap first mentions of technologies such as JSON in a link to an Understanding doc 

For example, [JSON][u-json].


## Set up a build plan at http://drone.io

For example, https://drone.io/github.com/springframework-meta/gs-rest-service

Be sure to set up the zapier integration to publish failed build reports to hipchat. This means simply sending email to sagan-ci.Xv6i@zapiermail.com from the settings > notification screen in your build. See https://drone.io/github.com/springframework-meta/gs-rest-service/admin/notifications for an example.


## Set up hipchat webhook for your repository

----

## Misc

- Use 'hello' as the package
- Keep the package hierarchy flat unless there is a strong reason to add subpackages
- Use 'Application' as the name of the class with a `main()` method
- Single blank line between `package` declaration and `import` declarations
- Single blank line between `import` declarations and class declaration.
- classpath lowercase, not CLASSPATH.
- Write a test script for the `complete` version of the code


## 4 spaces, not tabs (Java, XML, and all other file types)

- 4-space indent in code
- Allows consistent, precise display of content
- No surprises when copying and pasting (i.e. markdown renders tabs as spaces, users paste spaces, but `complete` version has tabs, so when user diffs to check their work they get whitespace differences, etc)

## Preface code blocks with a relative path/to/FileName.java header.

For example, `src/main/java/hello/Greeting.java` 


## Leave no newline between final closing braces

```java
public class Foo {

    public static void main(String... args) {
        // ...
    }
}
```


## Always add github project description for GSG repos


## Add .gitignore file to any empty directories under 'initial'


## Use markdown blockquote syntax for indicating note/tip/warning/info sections

For example:

> **Tip:** This is a tip, and should be colored appropriately (maybe green), and given a border, etc.

Precedent: http://developer.android.com/sdk/installing/bundle.html


