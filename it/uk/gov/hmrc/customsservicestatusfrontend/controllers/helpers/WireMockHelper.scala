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

package uk.gov.hmrc.customsservicestatusfrontend.controllers.helpers

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.client.{MappingBuilder, ResponseDefinitionBuilder}
import com.github.tomakehurst.wiremock.stubbing.StubMapping

trait WireMockHelper {
  this: BaseISpec =>

  def stub(method: MappingBuilder, response: ResponseDefinitionBuilder): StubMapping =
    stubFor(method.willReturn(response))

  def stubGet(uri: String, responseBody: String): StubMapping =
    stub(get(urlEqualTo(uri)), okJson(responseBody))

  def stubGetNotFound(uri: String): StubMapping =
    stub(get(urlEqualTo(uri)), notFound)

  def stubPost(url: String, status: Integer, responseBody: String): StubMapping = {
    removeStub(post(urlMatching(url)))
    stubFor(
      post(urlMatching(url))
        .willReturn(
          aResponse().withStatus(status).withBody(responseBody)
        )
    )
  }

  def stubPut(url: String, responseStatus: Integer, responseBody: String): StubMapping = {
    removeStub(put(urlMatching(url)))
    stubFor(
      put(urlMatching(url))
        .willReturn(
          aResponse().withStatus(responseStatus).withBody(responseBody)
        )
    )
  }
}
