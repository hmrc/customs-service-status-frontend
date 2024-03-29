import sbt._

object AppDependencies {

  private val bootstrapVersion = "8.4.0"
  private val hmrcMongoVersion = "1.7.0"
  private val playVersion = "play-30"

  val compile = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-frontend-$playVersion"         % bootstrapVersion,
    "uk.gov.hmrc"             %% s"play-frontend-hmrc-$playVersion"         % "9.1.0",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-30"                       % hmrcMongoVersion
  )

  val test = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-test-$playVersion"     % bootstrapVersion    % "test, it",
    "org.scalamock"           %% "scalamock"                        % "5.1.0"             % Test,
    "org.jsoup"               %  "jsoup"                            % "1.16.1"            % Test
  )
}
