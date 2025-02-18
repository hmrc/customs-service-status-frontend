import sbt._

object AppDependencies {

  private val bootstrapVersion = "9.7.0"
  private val hmrcMongoVersion = "2.2.0"
  private val playVersion = "play-30"

  val compile = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-frontend-$playVersion"         % bootstrapVersion,
    "uk.gov.hmrc"             %% s"play-frontend-hmrc-$playVersion"         % "11.2.0",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-30"                       % hmrcMongoVersion
  )

  val test = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-test-$playVersion"     % bootstrapVersion    % "test, it",
    "org.scalamock"           %% "scalamock"                        % "6.0.0"             % Test,
    "org.jsoup"               %  "jsoup"                            % "1.18.1"            % Test
  )
}
