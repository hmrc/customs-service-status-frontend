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

import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.Json
import uk.gov.hmrc.customsservicestatusfrontend.controllers.helpers.BaseISpec
import uk.gov.hmrc.customsservicestatusfrontend.TestData.{serviceStatuses, validPlannedOutageData, validUnplannedOutageData}
import uk.gov.hmrc.customsservicestatusfrontend.models.OutageData

class DashboardControllerISpec extends BaseISpec with MockitoSugar {

  val controller: DashboardController = app.injector.instanceOf[DashboardController]

  "show" should {
    "return the correct status with OutageData for the Dashboard page" in {
      stubGet("/customs-service-status/services", Json.stringify(Json.toJson(serviceStatuses)))
      stubGet("/customs-service-status/outages/latest?outageType=Planned", Json.stringify(Json.toJson(validPlannedOutageData)))
      stubGet("/customs-service-status/outages/latest?outageType=Unplanned", Json.stringify(Json.toJson(validUnplannedOutageData)))

      val result = controller.show()(fakeRequest(routes.DashboardController.show))

      status(result)      shouldBe OK
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }
  }
}
