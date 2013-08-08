#!/bin/sh

# Use spring.profiles.active to switch between main site and indexer app, e.g.
# --spring.profiles.active=indexer for indexer.  Site runs in default and site profiles.

which java  1>&2
ls $HOME/.jdk 1>&2

java -Xmx1260m -XX:MaxPermSize=512m -classpath "." org.springframework.boot.loader.JarLauncher $*