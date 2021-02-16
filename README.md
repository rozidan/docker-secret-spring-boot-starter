# Spring Boot Docker Secret Starter

> A Spring Boot launcher that allows you to read the contents of Docker containers secrets into your Spring Boot application.

[![Build Status](https://travis-ci.org/rozidan/docker-secret-spring-boot-starter.svg?branch=master)](https://travis-ci.org/rozidan/docker-secret-spring-boot-starter)
[![Coverage Status](https://coveralls.io/repos/github/rozidan/docker-secret-spring-boot-starter/badge.svg?branch=master)](https://coveralls.io/github/rozidan/docker-secret-spring-boot-starter?branch=master)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.rozidan/docker-secret-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.rozidan/docker-secret-spring-boot-starter/)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/oss.sonatype.org/com.github.rozidan/docker-secret-spring-boot-starter.svg)](https://oss.sonatype.org/content/repositories/snapshots/com/github/rozidan/docker-secret-spring-boot-starter/)

[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

## Features

Register the docker-secret starter to your Spring Boot application and reads docker container secrets content to spring
properties before it is starting up

## Setup

In order to add docker-secret to your project simply add this dependency to your classpath:

```xml
<dependency>
    <groupId>com.github.rozidan</groupId>
    <artifactId>docker-secret-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

```groovy
compile 'com.github.rozidan:docker-secret-spring-boot-starter:1.0.0'
```

For snapshots versions add the sonatype public repository:

```groovy
repositories {
    mavenCentral()
    maven { url "https://oss.sonatype.org/content/groups/public" }
    ...
}
```

### How it works?

The docker-secrets starter reads the secrets before the application is starting up and adds/overrides spring properties
with their content according to the file names.
<br>
For example, in case there is a secret called "spring.my.prop", the property "my.prop" will be added or overrode and its
content will be the secret file content.

### Which secrets are going to be Spring property?

There are 2 different prefixes for secrets that will be converted to Spring properties:

* "spring.*" - the property content will be exactly as secret content.
* "spring64.*" - the property content will be base64 decoded.

### From which folder will the secrets be extracted?

The secrets' folder path is represented by a system property/env called "secrets.path".

Example for classpath folder:

```shell
secrets.path=classpath:/secretstests/*
```

Example for system folder:

```shell
secrets.path=file:/run/secrets/*
```

> **NOTE:** The default path for Docker secrets is "/run/secrets/*"
<br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 
> and it's the default path in case 'secrets.path' is not defined

### Docker container configuration

Example for a secret config within Docker compose:

```yaml
secrets:
   - source: mariadb-pass
     target: spring.my.database.pass
```      

## License

[Apache-2.0](http://www.apache.org/licenses/LICENSE-2.0)