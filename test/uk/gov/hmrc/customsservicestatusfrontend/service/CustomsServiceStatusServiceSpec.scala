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
import uk.gov.hmrc.customsservicestatusfrontend.helpers.TestData.customsServiceStatusAll
import uk.gov.hmrc.customsservicestatusfrontend.models.CustomsServiceStatusAll
import uk.gov.hmrc.customsservicestatusfrontend.services.CustomsServiceStatusService
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future

class CustomsServiceStatusServiceSpec extends BaseSpec {

  val mockConnector = mock[CustomsServiceStatusConnector]

  val service = new CustomsServiceStatusService(mockConnector)

  "getStatus" should {
    "return response as expected" in {
      (mockConnector
        .getStatus()(_: HeaderCarrier))
        .expects(*)
        .returns(Future.successful(customsServiceStatusAll))

      service.getStatus().futureValue shouldBe customsServiceStatusAll
    }

    "handle any backend exceptions and return empty statuses" in {
      (mockConnector
        .getStatus()(_: HeaderCarrier))
        .expects(*)
        .returns(Future.failed(new RuntimeException("unexpected from backend")))

      service.getStatus().futureValue shouldBe CustomsServiceStatusAll(List.empty)
    }
  }
}
