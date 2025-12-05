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

import play.api.i18n.Messages
import uk.gov.hmrc.customsservicestatusfrontend.views.html.ErrorTemplate

class ErrorTemplateViewSpec extends ViewBehaviours {

  "service unavailable page" when {
    "rendered" should {

      val errorTemplatePage = ErrorTemplate(layout)

      val view = errorTemplatePage(
        s"${messages("manage_dashboard.title")} - ${messages("service.name")} - ${messages("service.title.suffix")}",
        messages("manage_dashboard.heading"),
        messages("service_unavailable.message") + Messages("service_unavailable.p1", messages("service_unavailable.logging_in.label"))
      )

      val document = view.asDocument

      behave like normalPage("manage_dashboard.heading")(view)
      behave like pageWithoutBackLink(view)
      behave like pageWithPageNotWorkingLink(view)

      "be the service unavailable HTML" in {

        document.getElementsByClass("govuk-body").text() should include(
          "The Check GVMS availability service is not working right now. You might still be able to use the Goods Vehicle Movement Service (GVMS)."
        )
        document.getElementsByClass("govuk-body").text() should include("You can check if GVMS is working by logging in.")
      }
    }
  }

}
