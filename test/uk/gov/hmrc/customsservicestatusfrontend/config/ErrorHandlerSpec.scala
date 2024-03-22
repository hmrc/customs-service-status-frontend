/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.customsservicestatusfrontend.config

import org.jsoup.Jsoup
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest

class ErrorHandlerSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.jvm"     -> false,
        "metrics.enabled" -> false
      )
      .build()

  private val fakeRequest = FakeRequest("GET", "/")

  private val handler = app.injector.instanceOf[ErrorHandler]

  "standardErrorTemplate" should {
    "render HTML" in {
      val html = handler.standardErrorTemplate("title", "heading", "message")(fakeRequest)
      html.contentType shouldBe "text/html"
    }
  }

  "internalServerErrorTemplate" should {
    "render service unavailable HTML" in {

      val html = handler.internalServerErrorTemplate(fakeRequest)
      html.contentType shouldBe "text/html"

      val doc = Jsoup.parse(html.body)
      doc.getElementsByClass("govuk-heading-l").text() shouldBe "Sorry, there is a problem with the service"
      doc.getElementsByClass("govuk-body").text() should include(
        "The Check GVMS availability service is not working right now. You might still be able to use the Goods Vehicle Movement Service (GVMS)."
      )
      doc.getElementsByClass("govuk-body").text() should include("You can check if GVMS is working by logging in.")
      doc
        .getElementById("gvms_service_url")
        .attr("href") shouldBe "https://www.gov.uk/guidance/get-a-goods-movement-reference#get-a-goods-movement-reference"

    }
  }
}
