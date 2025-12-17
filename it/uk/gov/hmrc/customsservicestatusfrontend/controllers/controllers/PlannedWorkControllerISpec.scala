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

package uk.gov.hmrc.customsservicestatusfrontend.controllers.controllers

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.libs.json.Json
import uk.gov.hmrc.customsservicestatusfrontend.TestData.*
import uk.gov.hmrc.customsservicestatusfrontend.controllers.helpers.BaseISpec
import uk.gov.hmrc.customsservicestatusfrontend.controllers.routes
import uk.gov.hmrc.customsservicestatusfrontend.models.DetailType.CommsText
import uk.gov.hmrc.customsservicestatusfrontend.models.OutageData

class PlannedWorkControllerISpec extends BaseISpec {

  val fakePlannedWorks: List[OutageData] = List(
    fakePlannedWork.copy(commsText = CommsText("Test one")),
    fakePlannedWork.copy(commsText = CommsText("Test two")),
    fakePlannedWork.copy(commsText = CommsText("Test three"))
  )

  "show" should {
    "show planned works if they exist" in {
      stubGet("/customs-service-status/services/planned-work", Json.stringify(Json.toJson(fakePlannedWorks)))
      val result = callRoute(fakeRequest(routes.PlannedWorkController.show))

      val document: Document = Jsoup.parse(contentAsString(result))
      document.text() shouldNot include("No maintenance work is planned at the moment")
      document.text() should include("Test one")
      document.text() should include("Test two")
      document.text() should include("Test three")
    }
    "show a single message if no planned works exist" in {
      stubGet("/customs-service-status/services/planned-work", Json.stringify(Json.toJson(List.empty[OutageData])))
      val result = callRoute(fakeRequest(routes.PlannedWorkController.show))

      val document: Document = Jsoup.parse(contentAsString(result))
      document.text() should include("No maintenance work is planned at the moment")
    }
  }
}
