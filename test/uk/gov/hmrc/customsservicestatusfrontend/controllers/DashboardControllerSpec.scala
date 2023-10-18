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

package uk.gov.hmrc.customsservicestatusfrontend.controllers

import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import uk.gov.hmrc.customsservicestatusfrontend.helpers.ControllerBaseSpec
import uk.gov.hmrc.customsservicestatusfrontend.helpers.TestData.serviceStatuses
import uk.gov.hmrc.customsservicestatusfrontend.services.StatusService
import uk.gov.hmrc.customsservicestatusfrontend.utils.Formatters
import uk.gov.hmrc.customsservicestatusfrontend.views.html.DashboardPage
import uk.gov.hmrc.http.HeaderCarrier

import java.time.Instant
import scala.concurrent.Future

class DashboardControllerSpec extends ControllerBaseSpec {

  private val fakeRequest = FakeRequest("GET", "/service-availability")
  private val dashboardPage: DashboardPage = app.injector.instanceOf[DashboardPage]
  private val mockService = mock[StatusService]

  private val controller = new DashboardController(
    stubMessagesControllerComponents(),
    dashboardPage,
    mockService
  )

  "GET /service-availability" should {
    "return success response as expected" in {
      val now = Instant.now()
      (mockService
        .getStatus()(_: HeaderCarrier))
        .expects(*)
        .returns(Future.successful(serviceStatuses))

      val result = controller.show(fakeRequest)
      status(result) shouldBe Status.OK

      val doc = Jsoup.parse(contentAsString(result))
      doc.getElementsByClass("govuk-heading-xl").text()           shouldBe "Service availability"
      doc.getElementsByClass("govuk-heading-m").text()            shouldBe "GVMS component status"
      doc.getElementsByClass("govuk-table__header").get(0).text() shouldBe "Component"
      doc.getElementsByClass("govuk-table__header").get(1).text() shouldBe "Availability status"
      doc.getElementsByClass("govuk-table__header").get(2).text() shouldBe "Last updated"

      doc
        .getElementsByClass("govuk-inset-text")
        .text()                                                   shouldBe s"Last updated: ${Formatters.instantFormatHours(now)}. Refresh this page for the latest availability status."
      doc.getElementsByClass("govuk-table__header").get(3).text() shouldBe "Haulier"
      doc.getElementsByClass("govuk-table__cell").text()            should include("OK")
      doc.getElementsByClass("govuk-table__cell").text()            should include(Formatters.instantFormatDate(now))
    }
  }
}
