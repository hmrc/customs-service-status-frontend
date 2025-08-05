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

package uk.gov.hmrc.customsservicestatusfrontend.service

import uk.gov.hmrc.customsservicestatusfrontend.connectors.CustomsServiceStatusConnector
import uk.gov.hmrc.customsservicestatusfrontend.helpers.BaseSpec
import uk.gov.hmrc.customsservicestatusfrontend.TestData.serviceStatuses
import uk.gov.hmrc.customsservicestatusfrontend.services.StatusService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class StatusServiceSpec extends BaseSpec {

  val mockConnector: CustomsServiceStatusConnector = mock[CustomsServiceStatusConnector]

  val service = new StatusService(mockConnector)

  "getStatus" should {
    "return response as expected" in {
      (mockConnector
        .getStatus()(_: HeaderCarrier))
        .expects(*)
        .returns(Future.successful(serviceStatuses))

      service.getStatus().futureValue shouldBe serviceStatuses
    }
  }
}
