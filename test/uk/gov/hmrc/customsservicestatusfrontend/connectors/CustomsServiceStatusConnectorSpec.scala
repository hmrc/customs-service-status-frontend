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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import play.api.http.Status
import play.api.libs.json.Json
import uk.gov.hmrc.customsservicestatusfrontend.helpers.BaseSpec
import uk.gov.hmrc.customsservicestatusfrontend.helpers.TestData.{serviceStatuses, validUnplannedOutageData}
import uk.gov.hmrc.customsservicestatusfrontend.models.OutageType.{Planned, Unplanned}
import uk.gov.hmrc.customsservicestatusfrontend.models.{OutageData, ServiceStatuses}
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, UpstreamErrorResponse}

import java.net.URL
import scala.concurrent.{ExecutionContext, Future}

class CustomsServiceStatusConnectorSpec extends BaseSpec {

  val mockHttpClient:    HttpClientV2   = mock[HttpClientV2]
  val mockRequestHolder: RequestBuilder = mock[RequestBuilder]

  val url = "http://localhost:8991/customs-service-status"

  val connector = new CustomsServiceStatusConnector(mockHttpClient, url)

  "getStatus" should {
    "return response as expected" in {

      (mockHttpClient.get(_: URL)(_: HeaderCarrier)).expects(*, *).returns(mockRequestHolder)
      (mockRequestHolder.execute(_: HttpReads[ServiceStatuses], _: ExecutionContext)).expects(*, *).returns(Future.successful(serviceStatuses))

      connector.getStatus().futureValue shouldBe serviceStatuses
    }
  }

  "getLatest" should {
    "return unplanned OutageData as expected" in {
      (mockHttpClient.get(_: URL)(_: HeaderCarrier)).expects(*, *).returns(mockRequestHolder)
      (mockRequestHolder
        .execute(_: HttpReads[Option[OutageData]], _: ExecutionContext))
        .expects(*, *)
        .returns(Future.successful(Some(validUnplannedOutageData)))

      connector.getLatest(Unplanned).futureValue shouldBe Some(validUnplannedOutageData)

    }

    "return planned OutageData as expected" in {
      (mockHttpClient.get(_: URL)(_: HeaderCarrier)).expects(*, *).returns(mockRequestHolder)
      (mockRequestHolder
        .execute(_: HttpReads[Option[OutageData]], _: ExecutionContext))
        .expects(*, *)
        .returns(Future.successful(Some(validUnplannedOutageData)))

      connector.getLatest(Planned).futureValue shouldBe Some(validUnplannedOutageData)
    }

    "return 404 when there is no unplanned OutageData" in {
      (mockHttpClient.get(_: URL)(_: HeaderCarrier)).expects(*, *).returns(mockRequestHolder)
      (mockRequestHolder
        .execute(_: HttpReads[Option[OutageData]], _: ExecutionContext))
        .expects(*, *)
        .returns(Future.successful(None))

      connector.getLatest(Unplanned).futureValue shouldBe None
    }

    "return 404 when there is no planned OutageData" in {
      (mockHttpClient.get(_: URL)(_: HeaderCarrier)).expects(*, *).returns(mockRequestHolder)
      (mockRequestHolder
        .execute(_: HttpReads[Option[OutageData]], _: ExecutionContext))
        .expects(*, *)
        .returns(Future.successful(None))

      connector.getLatest(Planned).futureValue shouldBe None
    }

    "return errors when there is an exception" in {
      val errorResponse = HttpResponse(status = Status.BAD_REQUEST)

      (mockHttpClient.get(_: URL)(_: HeaderCarrier)).expects(*, *).returns(mockRequestHolder)
      (mockRequestHolder
        .execute(_: HttpReads[HttpResponse], _: ExecutionContext))
        .expects(*, *)
        .returns(Future.successful(errorResponse))

      intercept[Exception](
        connector.getLatest(Unplanned).futureValue
      )
    }
  }
}
