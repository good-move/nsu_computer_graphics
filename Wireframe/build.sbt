name := "Wireframe"
version := "0.0.1"
scalaVersion := "2.12.5"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

libraryDependencies ++= Seq(
  // config
  "com.typesafe" % "config" % "1.3.0",
  "com.github.pureconfig" %% "pureconfig" % "0.9.1",

  // logging
  "org.slf4j" % "slf4j-api" % "1.7.12",

  // scalafx
  "org.scalafx" %% "scalafx" % "8.0.144-R12",
  "org.scalafx" %% "scalafxml-core-sfx8" % "0.4",

  "org.scalanlp" %% "breeze" % "0.13.2",

  // Native libraries are not included by default. add this if you want them (as of 0.7)
  // Native libraries greatly improve performance, but increase jar sizes.
  // It also packages various blas implementations, which have licenses that may or may not
  // be compatible with the Apache License. No GPL code, as best I know.
  "org.scalanlp" %% "breeze-natives" % "0.13.2",

  // The visualization library is distributed separately as well.
  // It depends on LGPL code
  "org.scalanlp" %% "breeze-viz" % "0.13.2"
)

// macros resolution
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
