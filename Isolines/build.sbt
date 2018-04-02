name := "Isolines"
version := "0.1"
scalaVersion := "2.12.4"

unmanagedJars in Compile += Attributed.blank(
  file(System.getenv("JAVA_HOME") + "/jre/lib/ext/jfxrt.jar")
)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "8.0.144-R12",
  "org.scalafx" %% "scalafxml-core-sfx8" % "0.4"
)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
