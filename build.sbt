lazy val root = (project in file(".")).
  settings(
    name := "scage-base",
    libraryDependencies += "io.dylemma" %% "scala-frp" % "1.1"
  )
