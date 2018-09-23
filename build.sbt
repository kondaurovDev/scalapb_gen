lazy val json_schema = (project in file("json_schema"))
  .enablePlugins(SbtTwirl)
  .settings(
    name := "json_schema",
    libraryDependencies ++= Seq(
      Dependencies.scalapb_compiler,
      Dependencies.json4sJackson
    )
  )

lazy val generator = (project in file("generator"))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "generator",
    libraryDependencies ++= Seq(
      "com.github.os72" % "protoc-jar" % "3.6.0"
    )
  ).dependsOn(json_schema)

lazy val root = (project in file("."))
  .aggregate(
    json_schema,
    generator
  )

lazy val test_protos = (project in file("test_protos"))
  .settings(
    PB.targets in Compile := Seq(
      scalapb.gen(flatPackage = true) -> (sourceManaged in Compile).value
    ),
    libraryDependencies ++= Seq(
      Dependencies.scalatest
    )
  ).dependsOn(json_schema)