# Getting Started Guides

## Understand

Getting Started Guides (GSGs) are:

- **task-focused**, ideally on tasks that developers would naturally Google when they're faced with implementing something they've never done before.

- **NOT Spring project-specific**. GSGs always *incorporate* one or more Spring projects, but they are all about using Spring to *solve a real-world problem*.

- the **hello world** of a given particular development task.

- about **copying and pasting your way to working code**. Users of any skill level, background, etc should be able to walk through GSGs in a context-free fashion, needing no prerequisites other than a JDK and a build system.

- designed to **capture users' attention and interest**, making them want to know more. GSGs connect to reference documentation, tutorials, and project pages, where users can drill into more detailed documentation.

- **optimized for competition**. GSGs should hold up well in inter-office technology bake-offs, e.g. web framework X vs. Spring @MVC, Big Data productivity stack Y vs Spring XD, etc.

- a **complement** to [tutorials](../tutorials), [reference apps](../reference-apps), and project-specific [sample apps](../sample-apps).


## Use

GSGs will have a prominent place in the redesigned springframework.org. Here are the current wireframes:

 - [http://springframework.org/getting-started](http://share.axure.com/50MZ29/?Page=Getting_Started_Guide___REST_V3)
 - [http://springframework.org/getting-started/gs-rest-service](http://share.axure.com/50MZ29/?Page=Getting_Started_Guide___REST_V3)


## Develop

Each GSG lives in its own repository at GitHub under the [springframework-meta](https://github.com/springframework-meta) organization, for example:

 - <https://github.com/springframework-meta/gs-rest-service>

Please follow the GSG [author guidelines](guidelines.md).

To cut down on duplication of common text across GSGs, we have a simple collection of reusable [sections](sections.md). Please familiarize yourself with them and use as appropriate. Issue a pull request if you'd like to add a new section. Use the [`mdp`](https://github.com/springframework-meta/mdp) tool to process your README.md files and expand embedded sections into a single, readable document.

If you find yourself working across a large number of GSGs, read up on [managing multiple repositories with `mr`](repo-management.md)
