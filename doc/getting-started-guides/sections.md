# {!begin#prereq-editor-jdk-buildtools}
 - A favorite text editor or IDE
 - [JDK 6][jdk] or later
 - [Maven 3.0][mvn] or later

[jdk]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[mvn]: http://maven.apache.org/download.cgi
{!end#prereq-editor-jdk-buildtools}


# {!begin#prereq-editor-android-buildtools}
 - A favorite text editor or IDE
 - [Android SDK][sdk]
 - [Maven 3.0][mvn] or later
 - An Android device or Emulator

[sdk]: http://developer.android.com/sdk/index.html
[mvn]: http://maven.apache.org/download.cgi
{!end#prereq-editor-android-buildtools}


# {!begin#how-to-complete-this-guide}
How to complete this guide
--------------------------

Like all Spring's [Getting Started guides](/getting-started), you can start from scratch and complete each step, or you can bypass basic setup steps that are already familiar to you. Either way, you end up with working code.

To **start from scratch**, move on to [Set up the project](#scratch).

To **skip the basics**, do the following:

 - [Download][zip] and unzip the source repository for this guide, or clone it using [git](/understanding/git):
`git clone https://github.com/springframework-meta/{@project-name}.git`
 - cd into `{@project-name}/initial`
 - Jump ahead to [Create a resource representation class](#initial).

**When you're finished**, you can check your results against the code in `{@project-name}/complete`.
{!end#how-to-complete-this-guide}

# {!begin#android-dev-env}
<a name="android-dev-env"></a>
Install the Android development environment
----------------------------------------------

Building Android applications requires you to install the [Android SDK][sdk]. Installing the Android SDK also installs the AVD Manager, which provides a graphical user interface for creating and managing Android Virtual Devices (AVDs). 

### Install the Android SDK

1. Download the correct version of the [Android SDK][sdk] for your operating system from the Android web site.

2. Unzip the archive to a location of your choosing. For example, on Linux or Mac, you could place it in the root of your user directory. See the [Android Developers] web site for additional installation details.

3. Configure the `ANDROID_HOME` environment variable based on the location of the Android SDK. Additionally, consider adding `ANDROID_HOME/tools`, and  `ANDROID_HOME/platform-tools` to your PATH.

    Mac OS X:

    ```sh
    $ export ANDROID_HOME=/<installation location>/android-sdk-macosx
    $ export PATH=${PATH}:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
    ```

    Linux:

    ```sh
    $ export ANDROID_HOME=/<installation location>/android-sdk-linux
    $ export PATH=${PATH}:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
    ```

    Windows:

    ```sh
    set ANDROID_HOME=C:\<installation location>\android-sdk-windows
    set PATH=%PATH%;%ANDROID_HOME%\tools;%ANDROID_HOME%\platform-tools
    ```

### Install Android SDK platforms and packages

The [Android SDK][sdk] download does not include specific Android platforms. To run the code in this guide, you need to download and install the latest SDK Platform. You do this by using the Android SDK and AVD Manager that was installed from the previous step.

1. Open the **Android SDK Manager** window:

    ```sh
    $ android
    ```

    > Note: If this command does not open the *Android SDK Manager*, then your path is not configured correctly.

2. Select the checkbox for **Tools**.

3. Select the checkbox for the latest Android SDK, Android 4.2.2 (API Level 17) as of this writing.

4. Select the checkbox for the **Android Support Library** from the **Extras** folder.

5. Click the **Install packages...** button to complete the download and installation.

    > Note: You may want to install all the available updates, but be aware it will take longer, as each API level is a large download.

[sdk]: http://developer.android.com/sdk/index.html
[Android Developers]: http://developer.android.com/sdk/installing/index.html
[Platforms and Packages]: http://developer.android.com/sdk/installing/adding-packages.html
{!end#android-dev-env}


# {!begin#build-system-intro}
First you set up a basic build script. You can use any build system you like when building apps with Spring, but the code you need to work with [Maven](https://maven.apache.org) and [Gradle](http://gradle.org) is included here. If you're not familiar with either, refer to [Getting Started with Maven](../gs-maven/README.md) or [Getting Started with Gradle](../gs-gradle/README.md).
{!end#build-system-intro}


# {!begin#android-build-system-intro}
First you set up a basic build script. You can use any build system you like when building apps with Spring, but the code you need to work with [Maven](https://maven.apache.org) and [Gradle](http://gradle.org) is included here. If you're not familiar with either, refer to [Getting Started with Maven](../gs-maven-android/README.md) or [Getting Started with Gradle](../gs-gradle-android/README.md).
{!end#android-build-system-intro}


# {!begin#create-directory-structure-hello}
### Create the directory structure

In a project directory of your choosing, create the following subdirectory structure; for example, with `mkdir -p src/main/java/hello` on *nix systems:

    └── src
        └── main
            └── java
                └── hello
{!end#create-directory-structure-hello}


# {!begin#create-directory-structure-org-hello}
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
{!end#create-directory-structure-org-hello}

# {!begin#create-android-manifest}
### Create an Android manifest

The [Android Manifest] contains all the information required to run an Android application, and it cannot build without one.

[Android Manifest]: http://developer.android.com/guide/topics/manifest/manifest-intro.html
{!end#create-android-manifest}

# {!begin#build-status}
[![Build Status](https://drone.io/github.com/springframework-meta/{@project-name}/status.png)](https://drone.io/github.com/springframework-meta/{@project-name}/latest)
{!end#build-status}


# {!begin#related-resources}
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
{!end#related-resources}


# {!begin#bootstrap-starter-pom-disclaimer}
TODO: mention that we're using Spring Bootstrap's [_starter POMs_](../gs-bootstrap-starter) here.

> Note to experienced Maven users who don't use an external parent project: You can take it out later, it's just there to reduce the amount of code you have to write to get started.

{!end#bootstrap-starter-pom-disclaimer}


# {!begin#start-android-virtual-device}
Start an Android virtual device
----------------------------------

If you do not have an Android device for testing, you can use an [Android Virtual Device (AVD)][avd]. To do this, you must first install the Android SDK and install the corresponding SDK platforms and packages. See [Install the Android Development Environment](#android-dev-env).

### Create an AVD

The following command creates a new AVD based on Android 4.2.2 (API Level 17).

```sh
$ android create avd --name Default --target 29 --abi armeabi-v7a
```

### Start the AVD

Start the emulator using the Android Maven plugin:

```sh
$ mvn android:emulator-start
```

This command starts an emulator named "Default". Please be patient as the emulator takes a few moments to finish startup.

[sdk]: http://developer.android.com/sdk/index.html
[avd]: http://developer.android.com/tools/devices/index.html
[Platforms and Packages]: http://developer.android.com/sdk/installing/adding-packages.html
{!end#start-android-virtual-device}


# {!begin#build-and-run-android}
Build and run the client
------------------------

Once the emulator starts up, run the following command to invoke the code and see the results of the REST request:

```sh
$ mvn clean package android:deploy android:run
```

This command builds the Android app and runs it in the emulator.
{!end#build-and-run-android}


# {!begin#build-an-executable-jar}
### Build an executable JAR

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
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
```

The `start-class` property tells Maven to create a `META-INF/MANIFEST.MF` file with a `Main-Class: hello.Application` entry. This entry enables you to run the jar with `java -jar`.

The [Maven Shade plugin][maven-shade-plugin] extracts classes from all jars on the classpath and builds a single "über-jar", which makes it more convenient to execute and transport your service.

Now run the following to produce a single executable JAR file containing all necessary dependency classes and resources:

    mvn package

[maven-shade-plugin]: https://maven.apache.org/plugins/maven-shade-plugin
{!end#build-an-executable-jar}
