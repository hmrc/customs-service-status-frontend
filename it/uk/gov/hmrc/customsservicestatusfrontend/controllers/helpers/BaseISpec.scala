/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.customsservicestatusfrontend.controllers.helpers

import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.testkit.NoMaterializer
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Inside, Inspectors, LoneElement, OptionValues, Status as _}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.*
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.*
import play.api.test.*
import play.api.test.Helpers.route
import play.api.{Application, Mode}
import uk.gov.hmrc.http.SessionKeys

import scala.concurrent.ExecutionContext.global
import scala.concurrent.{ExecutionContext, Future}

abstract class BaseISpec
    extends AnyWordSpec
    with GuiceOneAppPerSuite
    with BeforeAndAfterEach
    with BeforeAndAfterAll
    with Matchers
    with Inspectors
    with ScalaFutures
    with DefaultAwaitTimeout
    with Writeables
    with EssentialActionCaller
    with RouteInvokers
    with LoneElement
    with Inside
    with OptionValues
    with Results
    with Status
    with HeaderNames
    with MimeTypes
    with HttpProtocol
    with HttpVerbs
    with ResultExtractors
    with WireMockHelper
    with AdditionalAppConfig
    with WireMockSupport
    with WireMockConfig {

  implicit lazy val system:       ActorSystem      = ActorSystem()
  implicit lazy val materializer: Materializer     = NoMaterializer
  implicit def ec:                ExecutionContext = global

  override implicit val patienceConfig: PatienceConfig =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(50, Millis))

  additionalAppConfig ++= Map(
    "metrics.enabled"        -> false,
    "auditing.enabled"       -> false,
    "mongodb.uri"            -> "mongodb://localhost:27017/customs-service-status-frontend-test",
    "play.http.errorHandler" -> "uk.gov.hmrc.customsservicestatusfrontend.config.ErrorHandler"
  )

  def fakeRequest(call: Call): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(call)

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure(additionalAppConfig.toMap)
      .in(Mode.Test)
      .build()

  def callRoute[A](req: Request[A])(implicit app: Application, w: Writeable[A]): Future[Result] = {
    val errorHandler = app.errorHandler
    route(app, req) match {
      case None => fail("Route does not exist")
      case Some(fResult) =>
        fResult.recoverWith { case t: Throwable =>
          errorHandler.onServerError(req, t)
        }
    }
  }
}
