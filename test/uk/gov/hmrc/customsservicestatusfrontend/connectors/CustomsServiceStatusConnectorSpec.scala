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

package uk.gov.hmrc.customsservicestatusfrontend.connectors

import uk.gov.hmrc.customsservicestatusfrontend.helpers.BaseSpec
import uk.gov.hmrc.customsservicestatusfrontend.helpers.TestData.customsServiceStatusAll
import uk.gov.hmrc.customsservicestatusfrontend.models.CustomsServiceStatusAll
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads}

import scala.concurrent.{ExecutionContext, Future}

class CustomsServiceStatusConnectorSpec extends BaseSpec {

  val mockHttpClient = mock[HttpClient]

  val url = "http://localhost:8991/customs-service-status"

  val connector = new CustomsServiceStatusConnector(mockHttpClient, url)

  "getStatus" should {
    "return response as expected" in {

      (mockHttpClient
        .GET(_: String, _: Seq[(String, String)], _: Seq[(String, String)])(
          _: HttpReads[CustomsServiceStatusAll],
          _: HeaderCarrier,
          _: ExecutionContext
        ))
        .expects(*, *, *, *, *, *)
        .returns(Future.successful(customsServiceStatusAll))

      connector.getStatus().futureValue shouldBe customsServiceStatusAll
    }
  }
}
