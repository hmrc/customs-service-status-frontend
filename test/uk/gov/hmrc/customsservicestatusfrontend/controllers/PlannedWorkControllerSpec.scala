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

package uk.gov.hmrc.customsservicestatusfrontend.controllers

import org.jsoup.Jsoup
import play.api.test.FakeRequest
import uk.gov.hmrc.customsservicestatusfrontend.helpers.ControllerBaseSpec
import uk.gov.hmrc.customsservicestatusfrontend.helpers.TestData.fakePlannedWork
import uk.gov.hmrc.customsservicestatusfrontend.services.PlannedWorkService
import uk.gov.hmrc.customsservicestatusfrontend.utils.Formatters
import uk.gov.hmrc.customsservicestatusfrontend.views.html.PlannedWorkPage
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class PlannedWorkControllerSpec extends ControllerBaseSpec {

  private val fakeRequest = FakeRequest("GET", "/service-availability/planned-work")
  private val mockService:     PlannedWorkService = mock[PlannedWorkService]
  private val plannedWorkPage: PlannedWorkPage    = app.injector.instanceOf[PlannedWorkPage]
  private val controller: PlannedWorkController = new PlannedWorkController(
    stubMessagesControllerComponents(),
    plannedWorkPage,
    mockService
  )

  "GET /service-availability/planned-work" should {
    "redirect to the planned work view and load planned works when the database is not empty" in {
      (mockService
        .getPlannedWorkService()(_: HeaderCarrier))
        .expects(*)
        .returns(Future.successful(List(fakePlannedWork)))

      val view = controller.show(fakeRequest)
      val doc  = Jsoup.parse(contentAsString(view))
      status(view) shouldBe OK

      val expectedDateFrom: String =
        s"${Formatters.instantFormatDate(fakePlannedWork.startDateTime)} at ${Formatters.instantFormatHours(fakePlannedWork.startDateTime)}"
      val expectedDateTo: String =
        s"${Formatters.instantFormatDate(fakePlannedWork.endDateTime.get)} at ${Formatters.instantFormatHours(fakePlannedWork.endDateTime.get)}"
      val expectedDetails: String = fakePlannedWork.commsText.html
      val link:            String = doc.getElementById("plannedPage-link").attr("href")

      doc.getElementsByClass("govuk-heading-l").text() shouldBe "Planned work that will affect GVMS"
      doc.getElementsByClass("govuk-body").text()      shouldBe "Return to Check GVMS availability page"
      doc.getElementsByTag("strong").text()              should include("From:")
      doc.getElementsByTag("div").text()                 should include(expectedDateFrom)
      doc.getElementsByTag("strong").text()              should include("To:")
      doc.getElementsByTag("div").text()                 should include(expectedDateTo)
      doc.getElementsByTag("strong").text()              should include("Details:")
      doc.getElementsByTag("div").text()                 should include(expectedDetails)
      link                                             shouldBe "/customs-service-status/service-availability"

    }

    "redirect to the planned work view and display a message when the database is empty" in {
      (mockService
        .getPlannedWorkService()(_: HeaderCarrier))
        .expects(*)
        .returns(Future.successful(List()))

      val view = controller.show(fakeRequest)
      val doc  = Jsoup.parse(contentAsString(view))
      status(view) shouldBe OK

      doc.getElementById("no-work-planned").text() shouldBe "No maintenance work is planned at the moment"
    }
  }

}
