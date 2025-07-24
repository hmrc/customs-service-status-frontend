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

package uk.gov.hmrc.customsservicestatusfrontend.service

import uk.gov.hmrc.customsservicestatusfrontend.connectors.CustomsServiceStatusConnector
import uk.gov.hmrc.customsservicestatusfrontend.helpers.BaseSpec
import uk.gov.hmrc.customsservicestatusfrontend.helpers.TestData.{serviceStatuses, validUnplannedOutageData}
import uk.gov.hmrc.customsservicestatusfrontend.services.{UnplannedOutageService, StatusService}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class UnplannedOutageServiceSpec extends BaseSpec {

  val mockConnector: CustomsServiceStatusConnector = mock[CustomsServiceStatusConnector]

  val service = new UnplannedOutageService(mockConnector)

  "getList" should {
    "return response as expected" in {
      (mockConnector
        .getList()(_: HeaderCarrier))
        .expects(*)
        .returns(Future.successful(validUnplannedOutageData))

      service.getList().futureValue shouldBe validUnplannedOutageData
    }
  }
}
