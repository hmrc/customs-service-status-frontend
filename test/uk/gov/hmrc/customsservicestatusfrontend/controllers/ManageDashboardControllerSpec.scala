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
import uk.gov.hmrc.customsservicestatusfrontend.views.html.ManageDashboardPage

class ManageDashboardControllerSpec extends ControllerBaseSpec {

  val view = new ManageDashboardPage(
    layout = layout
  )

  val controller = new ManageDashboardController(
    stubMessagesControllerComponents(),
    view
  )

  "show" should {

    "return OK and the correct view for a GET" in {

      val result = controller.show()(
        FakeRequest(routes.ManageDashboardController.show)
      )

      val document = Jsoup.parse(contentAsString(result))
      document.select("h1").text() shouldBe "GVMS availability"

      status(result) shouldBe OK
    }
  }

}
