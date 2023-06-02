import com.typesafe.sbt.SbtLicenseReport.autoImportImpl._
import com.typesafe.sbt.license.{DepLicense, DepModuleInfo, LicenseCategory, LicenseInfo}
import sbt.Keys.baseDirectory

import scala.sys.process._

ThisBuild / scalaVersion := "2.13.10"

// Versions

// Last versions before Akka 2.7.0
val akkaV = "2.6.20"
val akkaJdbcV = "5.1.0"
val akkaHttpV = "10.2.10"
val akkaManagementV = "1.1.4"

val clientJREAzulV = "17.34.19"
val clientJREJavaV = "17.0.3"
val slf4jV = "2.0.5"
val ammoniteV = "2.5.8"
val monocleV = "3.2.0"
val jacksonV = "2.14.2"
val jfxV = "17.0.2"
val chartFxV = "11.2.7"
val getdownV = "1.8.7"
val netlibV = "3.0.3"
val jodaV = "2.12.5"
val keycloakVersion = "16.1.1"
val kamonVersion = "2.6.0"
val tapirV = "1.2.12"

// Libraries
val commonsMath3 = "org.apache.commons" % "commons-math3" % "3.6.1"
val enumeratum = "com.beachape" %% "enumeratum" % "1.7.2" //this version needs to match tapir's version

val scalaTest = "org.scalatest" %% "scalatest" % "3.2.15" % Test



// Utils
lazy val einsum = module("einsum", path = "einsum", withResources = true)
  .settings(
    libraryDependencies ++= DepSeq(scalaTest)
  )



def module(id: String, withResources: Boolean = false, path: String = null): Project = {
  val base = Option(path).map(file).getOrElse(file(id))

  Project(id = id, base = base)
    .settings(Defaults.coreDefaultSettings)
    .settings(
      addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
      autoCompilerPlugins := true,
      scalacOptions := Seq(
        "-unchecked",
        "-deprecation",
        "-feature",
        "-Xfatal-warnings",
        // "-Ywarn-unused",
        "-Yrangepos",
      ),
      dependencyOverrides ++= Seq(
        "org.slf4j" % "slf4j-api" % slf4jV
      ),
      Compile / scalaSource := baseDirectory.value / "src",
      Compile / packageDoc / mappings := Seq(),
      fork := true,
      javaOptions += "-Xmx4g",
      Test / scalaSource := baseDirectory.value / "tests",
      Test / resourceDirectory := baseDirectory.value / "test-resources",
      Test / parallelExecution := false,
      Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oFDG"),
      // Scalatest "slowpoke". Every 30 seconds print out any tests that have been running for more than 60 seconds.
      Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-W", "60", "30"),

      // For details on what the opens/exports options below are needed for, refer to the client's GCTestHelper class
      Test / javaOptions ++= Seq(
        "-Dlogback.configurationFile=" + (file(".") / "config" / "logback.xml").absolutePath,
      ),
    )
    .settings(
      Seq(
        if (withResources) Some(Compile / resourceDirectory := baseDirectory.value / "resources") else None
      ).flatten: _*
    )
}


def DepSeq(modules: ModuleID*): Seq[ModuleID] = {
  modules.map {
    _.exclude("org.slf4j", "slf4j-log4j12")
      .exclude("com.sun.jdmk", "jmxtools")
      .exclude("javax.jms", "jms")
      .exclude("com.sun.jmx", "jmxri")
  }
}

