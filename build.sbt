name := "khoailangtotet_assessment"

version := "0.1"

scalaVersion := "2.12.7"
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
//libraryDependencies += "com.oracle.jdbc" %% "ojdbc8" % "12.2.0.1"
// Scala 2.11, 2.12, 2.13
libraryDependencies ++= Seq(
  "ch.qos.logback"  %  "logback-classic"   % "1.2.3"
)

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.41"

libraryDependencies ++= Seq(
  "com.oracle.ojdbc" % "ojdbc8" % "19.3.0.0"
)

//logging:
libraryDependencies ++= Seq(
  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.11.0",
  "org.apache.logging.log4j" % "log4j-core" % "2.11.0" % Runtime
)

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.4.0"
)