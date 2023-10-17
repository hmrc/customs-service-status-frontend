import sbt._

object AppDependencies {

  private val bootstrapVersion = "7.21.0"
  private val hmrcMongoVersion = "1.3.0"

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28" % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc"         % "7.19.0-play-28",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-28"         % hmrcMongoVersion
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % bootstrapVersion    % "test, it",
    "org.scalamock"           %% "scalamock"                  % "5.1.0"             % Test,
    "org.jsoup"               %  "jsoup"                      % "1.13.1"            % Test
  )
}
