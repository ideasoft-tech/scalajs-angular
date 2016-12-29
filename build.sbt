name := "om-soap"

version := "1.0"

name := "om-soap"

scalaVersion := "2.11.8"


libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.1",
  "com.greencatsoft" %%% "scalajs-angular" % "0.7",
  "com.lihaoyi" %%% "upickle" % "0.4.3")

jsDependencies ++= Seq(
  "org.webjars" % "angularjs" % "1.3.14" / "angular.js",
  "org.webjars" % "angular-foundation" % "0.3.0" / "mm-foundation.js",
  "org.webjars" % "angular-foundation" % "0.3.0" / "mm-foundation-tpls.js",
  "org.webjars" % "angularjs" % "1.3.14" / "angular-route.js" dependsOn "angular.js"
)

val a = project.in(file("."))
  .enablePlugins(ScalaJSPlugin)

