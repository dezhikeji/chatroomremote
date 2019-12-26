import com.typesafe.sbt.packager.docker._
import NativePackagerHelper._

name := "chatroomremote"

version := "1.2.2"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
//  "com.typesafe.akka" %% "akka-actor" % "2.4.10",
  "com.typesafe.akka" %% "akka-remote" % "2.4.10",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.6.3",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.3",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.6.3",
  "net.jpountz.lz4" % "lz4" % "1.3.0",
  "com.twitter" %% "chill-akka" % "0.9.2",
  "com.typesafe" % "config" % "1.3.0"
)

val root = (project in file(".")).enablePlugins(DockerPlugin).enablePlugins(JavaAppPackaging)

dockerExposedPorts := Seq(2552)

doc in Compile <<= target.map(_ / "none")


javaOptions in Universal ++= Seq(
  " -Dfile.encoding=utf-8"
)


mainClass in Compile := Some("sdk.SystemMain")

dockerCommands :=Seq(
  Cmd("FROM","livehl/java8"),
  Cmd("WORKDIR","/opt/docker"),
  ExecCmd("copy","opt/docker/", "/opt/docker/"),
  ExecCmd("CMD","bin/"+name.value)
)

packageName in Docker := packageName.value

dockerUpdateLatest  in Docker := true

dockerRepository :=Some("registry.cn-hangzhou.aliyuncs.com/cdhub")
