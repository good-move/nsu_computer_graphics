name := "Wireframe"
version := "0.0.1"
scalaVersion := "2.12.5"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

libraryDependencies ++= Seq(
  // config
  "com.typesafe" % "config" % "1.3.0",

  // logging
  "org.slf4j" % "slf4j-api" % "1.7.12",

  // scalafx
  "org.scalafx" %% "scalafx" % "8.0.144-R12",
  "org.scalafx" %% "scalafxml-core-sfx8" % "0.4"
)

// macros resolution
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
