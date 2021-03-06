plugins {
  java
  `maven-publish`
  signing
  checkstyle
  id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

val signRequired = !rootProject.property("dev").toString().toBoolean()

group = "tr.com.infumia"

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

checkstyle {
  configFile = file("checkstyle.xml")
}

tasks {
  compileJava {
    options.encoding = Charsets.UTF_8.name()
  }

  jar {
    define()
  }

  javadoc {
    options.encoding = Charsets.UTF_8.name()
    (options as StandardJavadocDocletOptions).tags("todo")
  }

  val javadocJar by creating(Jar::class) {
    dependsOn(javadoc)
    define(classifier = "javadoc")
    from(javadoc)
  }

  val sourcesJar by creating(Jar::class) {
    dependsOn(classes)
    define(classifier = "sources")
    from(sourceSets["main"].allSource)
  }

  build {
    dependsOn(jar)
    dependsOn(sourcesJar)
    dependsOn(javadocJar)
  }
}

repositories {
  mavenCentral()
  maven(PAPERMC)
  mavenLocal()
}

dependencies {
  compileOnly(lombokLibrary)
  compileOnly(annotationsLibrary)

  annotationProcessor(lombokLibrary)
  annotationProcessor(annotationsLibrary)

  testImplementation(lombokLibrary)
  testImplementation(annotationsLibrary)

  testAnnotationProcessor(lombokLibrary)
  testAnnotationProcessor(annotationsLibrary)

  compileOnly("io.papermc.paper:paper-api:1.18.2-R0.1-SNAPSHOT")
}

publishing {
  publications {
    val publication = create<MavenPublication>("mavenJava") {
      groupId = project.group.toString()
      artifactId = project.name
      version = project.version.toString()

      from(components["java"])
      artifact(tasks["sourcesJar"])
      artifact(tasks["javadocJar"])
      pom {
        name.set("Bukkit Inventory")
        description.set("A simple inventory library for Bukkit plugins.")
        url.set("https://infumia.com.tr/")
        licenses {
          license {
            name.set("MIT License")
            url.set("https://mit-license.org/license.txt")
          }
        }
        developers {
          developer {
            id.set("portlek")
            name.set("Hasan Demirta??")
            email.set("utsukushihito@outlook.com")
          }
        }
        scm {
          connection.set("scm:git:git://github.com/infumia/bukkitinventory.git")
          developerConnection.set("scm:git:ssh://github.com/infumia/bukkitinventory.git")
          url.set("https://github.com/infumia/bukkitinventory")
        }
      }
    }

    signing {
      isRequired = signRequired
      if (isRequired) {
        useGpgCmd()
        sign(publication)
      }
    }
  }
}

nexusPublishing {
  repositories {
    sonatype()
  }
}
