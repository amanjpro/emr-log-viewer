name := "emr-log-viewer"

version := "0.1"

scalaVersion := "2.13.0-M4"

scalacOptions := Seq("-deprecation", "-feature", "-unchecked")

libraryDependencies ++= Seq("com.amazonaws" % "aws-java-sdk" % "1.11.354")

