scalaVersion := "2.11.11"
// resolvers += "mmreleases" at "https://artifactory.mediamath.com/artifactory/libs-release-global"
// libraryDependencies += "com.lihaoyi" %%% "upickle" % "1.2.0"
libraryDependencies += "com.softwaremill.sttp.client" %%% "core" % "2.2.5"
nativeLinkStubs := true
// nativeGC := "none"
enablePlugins(ScalaNativePlugin)
