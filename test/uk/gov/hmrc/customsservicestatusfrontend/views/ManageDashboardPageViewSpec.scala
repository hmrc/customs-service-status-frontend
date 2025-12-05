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

import uk.gov.hmrc.customsservicestatusfrontend.views.html.ManageDashboardPage

class ManageDashboardPageViewSpec extends ViewBehaviours {

  "/service-availability page" when {
    "rendered" should {

      val manageDashboardPageView = ManageDashboardPage(layout)

      val view     = manageDashboardPageView()
      val document = view.asDocument

      behave like normalPage("manage_dashboard.heading")(view)
      behave like pageWithoutBackLink(view)
      behave like pageWithPageNotWorkingLink(view)

      "show content as expected" in {

        document.getElementsByClass("govuk-heading-s cards__item__header").text() should include("GVMS service status")
        document.getElementsByClass("govuk-body").text() should include("Current availability of the Goods Vehicle Movement Service (GVMS).")

        document.getElementsByClass("govuk-heading-s cards__item__header").text() should include("Planned work")
        document.getElementsByClass("govuk-body").text() should include("Planned service interruptions such as scheduled maintenance.")
      }
    }

  }

}
