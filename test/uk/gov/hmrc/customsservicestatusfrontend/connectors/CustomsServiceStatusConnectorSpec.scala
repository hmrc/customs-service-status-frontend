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

import org.mockito.Mockito.*
import uk.gov.hmrc.customsservicestatusfrontend.helpers.BaseSpec
import uk.gov.hmrc.customsservicestatusfrontend.helpers.TestData.{fakePlannedWork, serviceStatuses}
import uk.gov.hmrc.customsservicestatusfrontend.models.{OutageData, ServiceStatuses}
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.http.*

import java.net.URL
import scala.concurrent.{ExecutionContext, Future}

class CustomsServiceStatusConnectorSpec extends BaseSpec {

  val mockHttpClient:     HttpClientV2   = mock[HttpClientV2]
  val mockRequestBuilder: RequestBuilder = mock[RequestBuilder]

  val url = "http://localhost:8991/customs-service-status"

  val connector = new CustomsServiceStatusConnector(mockHttpClient, url)

  "getStatus" should {
    "return response as expected" in {

      (mockHttpClient.get(_: URL)(_: HeaderCarrier)).expects(*, *).returns(mockRequestBuilder)
      (mockRequestBuilder.execute(_: HttpReads[ServiceStatuses], _: ExecutionContext)).expects(*, ec).returns(Future.successful(serviceStatuses))

      connector.getStatus().futureValue shouldBe serviceStatuses
    }
  }

  "getPlannedWork" should {
    "return planned work" in {
      (mockHttpClient
        .get(_: URL)(_: HeaderCarrier))
        .expects(*, *)
        .returns(mockRequestBuilder)

      (mockRequestBuilder
        .execute(_: HttpReads[List[OutageData]], _: ExecutionContext))
        .expects(*, *)
        .returns(Future(List(fakePlannedWork)))

      connector.getPlannedWork().futureValue shouldBe List(fakePlannedWork)
    }
  }
}
