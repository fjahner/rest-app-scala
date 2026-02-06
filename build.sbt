import Dependencies._

ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := Versions.scala3
ThisBuild / organization := "com.github.fjahner"

lazy val root = (project in file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(
    name := "rest-app-scala",
    libraryDependencies ++= Dependencies.allDependencies,

    // BuildInfo configuration
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion),
    buildInfoPackage := "com.github.fjahner",

    // Compiler options for Scala 3
    scalacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-feature",
      "-unchecked",
      "-deprecation",
      "-Wunused:all",
      "-Wvalue-discard"
    ),

    // Test configuration
    Test / parallelExecution := false,
    Test / testOptions += Tests.Argument(TestFrameworks.MUnit, "-b")
  )
