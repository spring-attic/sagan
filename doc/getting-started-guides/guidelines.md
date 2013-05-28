## Use conventional style for git commits

See <https://github.com/SpringSource/spring-framework/blob/master/CONTRIBUTING.md#format-commit-messages>.

## Common sections in all GSGs

- What you'll build

- What you'll need

- {!snippet:how-to-complete-this-guide}

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

- Use imperative statements and sentence-style capitalization:
   
    - Set up the project
   
    - Create a configuration class
   
    - Run the service

- Follow a section heading with one or more lines of text -- i.e. no empty sections.

## Always create 'scratch' and 'initial' anchors

## Use snippets for common content

 1. Read the [mdp README](https://github.com/springframework-meta/mdp#readme)
 2. Browse available [snippets][snippets] for use in GSGs
 3. See their use in practice in the [gs-rest-service README](https://github.com/springframework-meta/gs-rest-service#readme)

[snippets]: https://github.com/springframework-meta/springframework.org/blob/master/doc/getting-started-guides/snippets.md


## Do not mention cloning or downloading the repository in your guide

Just include the [{!snippet:how-to-complete-this-guide}][snippets] snippet after the "What you'll need" section.

## Precede code block with intro sentence to give context

Precede code with at least one sentence that explains its purpose or what you should notice about it. More or most details can follow the code, but avoid starting a code block without giving context.

## Copy and paste your way to working code
At every step in the guide, the user should have code that compiles. In the end, the user should have code that runs.

## Demonstrate what 'working code' means
Always end the guide with a final step that exercises the working code. For example, a guide that stands up a RESTful web service should exercise that service using. 

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

## Misc

- Use 'hello' as the package.
- Keep the package hierarchy flat unless there is a strong reason to add subpackages
- Use 'Application' as the name of the class with a `main()` method
- Single blank line between `package` declaration and `import` declarations
- Single blank line between `import` declarations and class declaration.
- classpath lowercase, not CLASSPATH.
- Write a test script for the `complete` version of the code



## Naming

Use `initial` and `complete` for directory names.

Use group id `org.springframework`.

In the `initial/pom.xml`, use `<artifactId>gs-${name}</artifactId>`; for example, `gs-rest-service`.

in the `complete/pom.xml`, use `<artifactId>gs-${name}-complete</artifactId>; for example, `gs-rest-service-complete`.

## Versioning

Use 0.1.0 for all versions. This is consistent with the [semantic versioning](http://semver.org) approach and indicates a very early stage of development, which is what Getting Started Guides are all about.


## Use consistent versioning in links

3.2.x vs current, etc

## Use reference-style links as a rule

    a sentence with [an embedded link][linkid]
    ...
    [linkid]: http://abc.com/xyz

http://daringfireball.net/projects/markdown/syntax

##Tone and voice
- Use "you" and "your" rather than "we", "our", "us", and "let's". "You" is more direct and accurate; the guide is addressing and is written for the user, "you."
- Use present tense except in a context where it could be inaccurate or misleading.
- Use active voice rather than passive whenever possible, especially when you're referring to something the user does. 


## Wrap first mentions of technologies such as JSON in a link to an Understanding doc 

For example, [JSON][u-json].


## Set up a build plan at http://drone.io

For example, https://drone.io/github.com/springframework-meta/gs-rest-service

Be sure to set up the zapier integration to publish failed build reports to hipchat. This means simply sending email to sagan-ci.Xv6i@zapiermail.com from the settings > notification screen in your build. See https://drone.io/github.com/springframework-meta/gs-rest-service/admin/notifications for an example.

## Set up hipchat webhook for your repository

----

## Always add github project description for GSG repos

## Use a common .gitignore

You can probably create this from a gs-template project after all...
for now, use the .gitignore in gs-rest-service.

## Add .gitignore file to any empty directories under 'initial'

## Use markdown blockquote syntax for indicating note/tip/warning/info sections

For example:

> **Tip:** This is a tip, and should be colored appropriately (maybe green), and given a border, etc.

Precedent: http://developer.android.com/sdk/installing/bundle.html



