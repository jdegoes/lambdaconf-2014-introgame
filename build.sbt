organization := "com.github.jdegoes.lambdaconf"

name := "introfp"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.4"

mainClass := Some("Main")

scalacOptions ++= Seq(
  "-feature",
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"), Resolver.sonatypeRepo("snapshots"),
  "JBoss repository" at "https://repository.jboss.org/nexus/content/repositories/"
)

libraryDependencies ++= Seq(
  "org.scalaz"      %% "scalaz-core"                % "7.1.0-SNAPSHOT",
  "org.scalaz"      %% "scalaz-concurrent"          % "7.1.0-SNAPSHOT",  
  "org.scalaz"      %% "scalaz-task"                % "7.1.0-SNAPSHOT",  
  "com.github.julien-truffaut"  %%  "monocle-core"  % "0.2-SNAPSHOT",
  "org.scalaz"      %% "scalaz-scalacheck-binding"  % "7.1.0-SNAPSHOT"  % "test",
  "org.scalacheck"  %% "scalacheck"                 % "1.10.1"  % "test",
  "org.specs2"      %% "specs2"                     % "2.3.4-scalaz-7.1.0-M3"   % "test"
)
