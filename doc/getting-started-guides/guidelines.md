## Use conventional git commit comment formatting

Please read and follow the [commit guidelines section of Pro Git][progit].

Most importantly, please format your commit messages in the following way
(adapted from the commit template in the link above):

    Summarize changes in 50 characters or less

    More detailed explanatory text, if necessary. Wrap it to about 72
    characters or so. In some contexts, the first line is treated as the
    subject of an email and the rest of the text as the body. The blank
    line separating the summary from the body is critical (unless you omit
    the body entirely); tools like rebase can get confused if you run the
    two together.

    Further paragraphs come after blank lines.

     - Bullet points are okay, too
     
     - Typically a hyphen or asterisk is used for the bullet, preceded by a
       single space, with blank lines in between, but conventions vary here


1. Use present-tense imperative statements in the subject line, e.g. "Fix broken Javadoc link"
1. Begin the subject line sentence with a capitalized verb, e.g. "Add, Prune, Fix, Introduce, Avoid, etc."
1. Do not end the subject line with a period
1. Keep the subject line to 50 characters or less if possible
1. Wrap lines in the body at 72 characters or less

Here's a good [example][]:

    Edit content and style

     - Revise all exposition
     - Remove gradle artifacts and instructions for now
     - Eliminate @Configuration class
     - Convert tabs to spaces in .java and .xml files
     - Preserve empty directory structure in 'initial'

Single-line, subject-only commit messages can be fine as well:

    Edit per guidelines

[progit]: http://progit.org/book/ch5-2.html#commit_guidelines
[example]: https://github.com/spring-guides/gs-rest-service/commit/f103b1701926341487233ecb3fdbca026f404d23

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

 1. Read the [mdp README](https://github.com/spring-guides/mdp#readme)
 2. Browse available [snippets][snippets] for use in GSGs
 3. See their use in practice in the [gs-rest-service README](https://github.com/spring-guides/gs-rest-service#readme)

[snippets]: https://github.com/spring-io/spring.io/blob/master/doc/getting-started-guides/snippets.md


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
- Wrap lines of source files (.java, .xml, etc) at no more than 110 characters. This is important to avoid horizontal scrolling on GitHub/sf.org.


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

For example, https://drone.io/github.com/spring-guides/gs-rest-service

Be sure to set up the zapier integration to publish failed build reports to hipchat. This means simply sending email to sagan-ci.Xv6i@zapiermail.com from the settings > notification screen in your build. See https://drone.io/github.com/spring-guides/gs-rest-service/admin/notifications for an example.

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
