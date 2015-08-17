lazy val root = (project in file(".")).
  settings(
    name := "scage-base",
    libraryDependencies += "io.dylemma" %% "scala-frp" % "1.1",
    libraryDependencies += "org.streum" %% "configrity-core" % "1.0.0"
  )
