import play.sbt.PlayImport.PlayKeys.playDefaultPort
import sbt.Keys.evictionErrorLevel
import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings

val appName = "customs-service-status-frontend"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(
    majorVersion := 0,
    playDefaultPort := 8993,
    scalaVersion := "3.3.3",
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalafmtOnCompile := true,
    pipelineStages := Seq(gzip)
  )
  .configs(IntegrationTest)
  .settings(integrationTestSettings() *)
  .settings(
    Compile / unmanagedResourceDirectories += baseDirectory.value / "resources",
    IntegrationTest / unmanagedSourceDirectories :=
      (IntegrationTest / baseDirectory)(base => Seq(base / "it", base / "test-common")).value,
    Test / unmanagedSourceDirectories := (Test / baseDirectory)(base => Seq(base / "test", base / "test-common")).value
  )
  .settings(CodeCoverageSettings.settings *)
  .settings( //fix scaladoc generation in jenkins
    scalacOptions += "-language:postfixOps",
    scalacOptions += "-no-indent"
  )
  .settings(
    addCommandAlias("runTestOnly", "run -Dplay.http.router=testOnlyDoNotUseInAppConf.Routes")
  )

evictionErrorLevel := Level.Warn
