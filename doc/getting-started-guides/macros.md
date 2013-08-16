<#macro prereq_editor_jdk_buildtools java_version="6">
 - A favorite text editor or IDE
 - [JDK ${java_version}][jdk] or later
 - [Gradle 1.7+][gradle] or [Maven 3.0+][mvn]

[jdk]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[gradle]: http://www.gradle.org/
[mvn]: http://maven.apache.org/download.cgi
</#macro>

<#macro prereq_editor_android_buildtools>
 - A favorite text editor or IDE
 - [Android SDK][sdk]
 - [Maven 3.0][mvn] or later
 - An Android device or Emulator

[sdk]: http://developer.android.com/sdk/index.html
[mvn]: http://maven.apache.org/download.cgi
</#macro>


<#macro how_to_complete_this_guide jump_ahead='Create a resource representation class'>
How to complete this guide
--------------------------

Like all Spring's [Getting Started guides](/guides/gs), you can start from scratch and complete each step, or you can bypass basic setup steps that are already familiar to you. Either way, you end up with working code.

To **start from scratch**, move on to [Set up the project](#scratch).

To **skip the basics**, do the following:

 - [Download][zip] and unzip the source repository for this guide, or clone it using [Git][u-git]:
`git clone https://github.com/springframework-meta/${project_id}.git`
 - cd into `${project_id}/initial`.
 - Jump ahead to [${jump_ahead}](#initial).

**When you're finished**, you can check your results against the code in `${project_id}/complete`.
[zip]: https://github.com/springframework-meta/${project_id}/archive/master.zip
<@u_git/>
</#macro>


<#macro build_system_intro>
First you set up a basic build script. You can use any build system you like when building apps with Spring, but the code you need to work with [Gradle](http://gradle.org) and [Maven](https://maven.apache.org) is included here. If you're not familiar with either, refer to [Building Java Projects with Gradle](/guides/gs/gradle/) or [Building Java Projects with Maven](/guides/gs/maven).
</#macro>

<#macro android_build_system_intro>
In this section you set up a basic build script and then create a simple application. 

> **Note:** If you are new to Android projects, before you proceed, refer to [Installing the Android Development Environment](/guides/gs/android/) to help you configure your development environment.

You can use any build system you like when building apps with Spring, but the code you need to work with [Gradle](http://gradle.org) and [Maven](https://maven.apache.org) is included here. If you're not familiar with either, refer to [Building Android Projects with Gradle](/guides/gs/gradle-android/) or [Building Android Projects with Maven](/guides/gs/maven-android/).
 
</#macro>


<#macro create_directory_structure_hello>
### Create the directory structure

In a project directory of your choosing, create the following subdirectory structure; for example, with `mkdir -p src/main/java/hello` on *nix systems:

    └── src
        └── main
            └── java
                └── hello
</#macro>

<#macro create_directory_structure_org_hello>
### Create the directory structure

In a project directory of your choosing, create the following subdirectory structure; for example, with the following command on Mac or Linux:

```sh
$ mkdir -p src/main/java/org/hello
```

    └── src
        └── main
            └── java
                └── org
                    └── hello
</#macro>

<#macro create_android_manifest>
### Create an Android manifest

The [Android Manifest] contains all the information required to run an Android application, and it cannot build without one.

[Android Manifest]: http://developer.android.com/guide/topics/manifest/manifest-intro.html
</#macro>

<#macro build_status>
[![Build Status](https://drone.io/github.com/springframework-meta/${project_id}/status.png)](https://drone.io/github.com/springframework-meta/${project_id}/latest)
</#macro>


<#macro related_resources>
Related resources
-----------------

There's more to building RESTful web services than is covered here. You may want to continue your exploration of Spring and REST with the following Getting Started guides:

* [Handling POST, PUT, and GET requests in REST services](TODO)
* [Creating self-describing APIs with HATEOAS](TODO)
* [Securing a REST service with HTTP Basic](TODO)
* [Securing a REST service with OAuth](TODO)
* [Consuming REST services](https://github.com/springframework-meta/gs-consuming-rest-core/blob/master/README.md)
* [Testing REST services](TODO)
</span>
</#macro>


<#macro bootstrap_starter_pom_disclaimer>
This guide is using [Spring Boot's starter POMs](/guides/gs/spring-boot/).
</#macro>

<#macro build_and_run_android>
Build and run the client
------------------------

With an attached device or emulator running, invoke the code and see the results of the REST request:

```sh
$ mvn clean package android:deploy android:run
```

The command builds the Android app and runs it in the emulator or attached device.
</#macro>


<#macro build_an_executable_jar_mainhead>
Build an executable JAR
-----------------------
</#macro>


<#macro build_an_executable_jar_subhead>
### Build an executable JAR
</#macro>

<#macro build_an_executable_jar>
Now that your `Application` class is ready, you simply instruct the build system to create a single, executable jar containing everything. This makes it easy to ship, version, and deploy the service as an application throughout the development lifecycle, across different environments, and so forth.

Add the following configuration to your existing Maven POM:

`pom.xml`
```xml
    <properties>
        <start-class>hello.Application</start-class>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
```

The `start-class` property tells Maven to create a `META-INF/MANIFEST.MF` file with a `Main-Class: hello.Application` entry. This entry enables you to run it with `mvn spring-boot:run` (or simply run the jar itself with `java -jar`).

The [Spring Boot maven plugin][spring-boot-maven-plugin] collects all the jars on the classpath and builds a single "über-jar", which makes it more convenient to execute and transport your service.

Now run the following command to produce a single executable JAR file containing all necessary dependency classes and resources:

```sh
$ mvn package
```

[spring-boot-maven-plugin]: https://github.com/SpringSource/spring-boot/tree/master/spring-boot-tools/spring-boot-maven-plugin

> **Note:** The procedure above will create a runnable JAR. You can also opt to [build a classic WAR file](/guides/gs/convert-jar-to-war/) instead.
</#macro>

<#macro build_an_executable_jar_with_gradle>
Now that your `Application` class is ready, you simply instruct the build system to create a single, executable jar containing everything. This makes it easy to ship, version, and deploy the service as an application throughout the development lifecycle, across different environments, and so forth.

Add the following dependency to your Gradle **build.gradle** file's `buildscript` section:

```groovy
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:0.5.0.BUILD-SNAPSHOT")
    }
```

Further down inside `build.gradle`, add the following to the list of plugins:

```groovy
apply plugin: 'spring-boot'
```

The [Spring Boot gradle plugin][spring-boot-gradle-plugin] collects all the jars on the classpath and builds a single "über-jar", which makes it more convenient to execute and transport your service.
It also searches for the `public static void main()` method to flag as a runnable class.

Now run the following command to produce a single executable JAR file containing all necessary dependency classes and resources:

```sh
$ ./gradlew build
```

Now you can run the JAR by typing:

```sh
$ java -jar build/libs/${project_id}-0.1.0.jar
```

[spring-boot-gradle-plugin]: https://github.com/SpringSource/spring-boot/tree/master/spring-boot-tools/spring-boot-gradle-plugin

> **Note:** The procedure above will create a runnable JAR. You can also opt to [build a classic WAR file](/guides/gs/convert-jar-to-war/) instead.
</#macro>

<#macro build_the_application>
Build the application
------------------------

To build this application, you need to add some extra bits to your pom.xml file.

```xml
	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-shade-plugin</artifactId>
				<version>2.1</version>
				<executions>
					<execution>
						<phase>package</phase>
						<goals>
							<goal>shade</goal>
						</goals>
						<configuration>
							<transformers>
								<transformer
									implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
									<mainClass>hello.Application</mainClass>
								</transformer>
							</transformers>
						</configuration>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>
```

The [Maven Shade plugin][maven-shade-plugin] extracts classes from all jars on the classpath and builds a single "über-jar", which makes it more convenient to execute and transport your service.

Now run the following to produce a single executable JAR file containing all necessary dependency classes and resources:

```sh
$ mvn package
```

[maven-shade-plugin]: https://maven.apache.org/plugins/maven-shade-plugin

> **Note:** The procedure above will create a runnable JAR. You can also opt to [build a classic WAR file](/guides/gs/convert-jar-to-war/) instead.
</#macro>

<#macro run_the_application_with_maven module="service">
Run the ${module}
-------------------
Run your ${module} using the spring-boot plugin at the command line:

```sh
$ mvn spring-boot:run
```

</#macro>

<#macro run_the_application_with_gradle module="service">
Run the ${module}
-------------------
Run your ${module} at the command line:

```sh
$ ./gradlew clean build && java -jar build/libs/${project_id}-0.1.0.jar
```

</#macro>

<#macro run_the_application module="application">
Run the ${module}
-------------------
Run your ${module} with `java -jar` at the command line:

```sh
$ java -jar target/${project_id}-0.1.0.jar
```

</#macro>

<#macro u_oauth>
[u-oauth]: /understanding/OAuth
</#macro>

<#macro u_application_context>
[u-application-context]: /understanding/application-context
</#macro>

<#macro u_git>
[u-git]: /understanding/Git
</#macro>

<#macro u_json>
[u-json]: /understanding/JSON
</#macro>

<#macro u_hateoas>
[u-hateoas]: /understanding/HATEOAS
</#macro>

<#macro u_amqp>
[u-amqp]: /understanding/AMQP
</#macro>

<#macro u_nosql>
[u-nosql]: /understanding/NoSQL
</#macro>

<#macro u_rest>
[u-rest]: /understanding/REST
</#macro>

<#macro u_tomcat>
[u-tomcat]: /understanding/Tomcat
</#macro>

<#macro u_view_templates>
[u-view-templates]: /understanding/view-templates
</#macro>

<#macro u_war>
[u-war]: /understanding/WAR
</#macro>

