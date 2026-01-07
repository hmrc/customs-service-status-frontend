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

package uk.gov.hmrc.customsservicestatusfrontend.views

import uk.gov.hmrc.customsservicestatusfrontend.TestData.*
import uk.gov.hmrc.customsservicestatusfrontend.models.OutageData
import uk.gov.hmrc.customsservicestatusfrontend.utils.Formatters
import uk.gov.hmrc.customsservicestatusfrontend.views.html.PlannedWorkView

class PlannedWorkViewSpec extends ViewBehaviours {

  "/service-availability/planned work page" when {

    val plannedWorkView = PlannedWorkView(govukLayoutTwoThirds)

    def view(plannedWorks: List[OutageData], availabilityForOtherServicesUrl: String) =
      plannedWorkView(plannedWorks, availabilityForOtherServicesUrl)

    List(
      (List(), availabilityForOtherServicesUrl),
      (List(plannedWork), "")
    ).foreach { (plannedWorks, availabilityForOtherServicesUrl) =>
      s"rendered, in the scenario where planned work: $plannedWorks" should {

        behave like normalPage("planned_work.title")(view(plannedWorks, availabilityForOtherServicesUrl))
        behave like pageWithoutBackLink(view(plannedWorks, availabilityForOtherServicesUrl))
        behave like pageWithPageNotWorkingLink(view(plannedWorks, availabilityForOtherServicesUrl))

      }
    }

    "show content with planned works as expected" in {

      val document = view(List(plannedWork), availabilityForOtherServicesUrl).asDocument

      val expectedDateFrom: String =
        s"${Formatters.instantFormatDate(plannedWork.startDateTime)} at ${Formatters.instantFormatHours(plannedWork.startDateTime)}"
      val expectedDateTo: String =
        s"${Formatters.instantFormatDate(plannedWork.endDateTime.get)} at ${Formatters.instantFormatHours(plannedWork.endDateTime.get)}"
      val expectedDetails: String = plannedWork.commsText.html
      val link:            String = document.getElementById("plannedPage-link").attr("href")

      document.getElementsByClass("govuk-heading-l").text() shouldBe "Planned work that will affect GVMS"
      document.getElementsByClass("govuk-body").text()      shouldBe "Return to Check GVMS availability page"
      document.getElementsByTag("strong").text()              should include("From:")
      document.getElementsByTag("div").text()                 should include(expectedDateFrom)
      document.getElementsByTag("strong").text()              should include("To:")
      document.getElementsByTag("div").text()                 should include(expectedDateTo)
      document.getElementsByTag("strong").text()              should include("Details:")
      document.getElementsByTag("div").text()                 should include(expectedDetails)
      link                                                  shouldBe "/customs-service-status/service-availability"

    }

    "show content when there is no planned work scheduled" in {

      val document = view(List(), availabilityForOtherServicesUrl).asDocument

      document.getElementsByClass("govuk-heading-l").text()         shouldBe "Planned work that will affect GVMS"
      document.getElementById("no-work-planned").text()             shouldBe "No maintenance work is planned at the moment."
      document.getElementsByClass("govuk-heading-m").first().text() shouldBe "View live service availability"
      document.getElementsByClass("govuk-heading-m").last().text()  shouldBe "Other HMRC services"
      document.getElementsByTag("a").text()                           should include("Track availability for other HMRC services")
      document.getElementsByTag("div").text()                         should include("Return to Check GVMS availability page")

    }
  }

}
