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

import uk.gov.hmrc.customsservicestatusfrontend.TestData.*
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import play.api.test.FakeRequest
import uk.gov.hmrc.customsservicestatusfrontend.helpers.ControllerBaseSpec
import uk.gov.hmrc.customsservicestatusfrontend.services.PlannedWorkService
import uk.gov.hmrc.customsservicestatusfrontend.views.html.PlannedWorkPage

import scala.concurrent.Future

class PlannedWorkControllerSpec extends ControllerBaseSpec {

  private val fakeRequest = FakeRequest("GET", "/service-availability/planned-work")
  private val mockService: PlannedWorkService = mock[PlannedWorkService]

  private val plannedWorkPage = new PlannedWorkPage(govukLayoutTwoThirds)

  private val controller: PlannedWorkController = new PlannedWorkController(
    stubMessagesControllerComponents(),
    plannedWorkPage,
    mockService,
    "https://www.gov.uk/government/collections/hm-revenue-and-customs-service-availability-and-issues"
  )

  "GET /service-availability/planned-work" should {
    "redirect to the planned work view and load planned works when the database is not empty" in {
      when(
        mockService
          .getAllPlannedWorks()(any())
      )
        .thenReturn(Future.successful(List(fakePlannedWork)))

      val view = controller.show(fakeRequest)
      val doc  = Jsoup.parse(contentAsString(view))

      status(view)                                     shouldBe OK
      doc.getElementsByClass("govuk-heading-l").text() shouldBe "Planned work that will affect GVMS"

    }

    "redirect to the planned work view and display a message when the database is empty" in {
      when(
        mockService
          .getAllPlannedWorks()(any())
      )
        .thenReturn(Future.successful(List()))

      val view = controller.show(fakeRequest)
      val doc  = Jsoup.parse(contentAsString(view))

      status(view)                                     shouldBe OK
      doc.getElementsByClass("govuk-heading-l").text() shouldBe "Planned work that will affect GVMS"
    }
  }

}
