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

package uk.gov.hmrc.customsservicestatusfrontend.models

import play.api.libs.json._
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.Instant

sealed trait State {
  val value: String = toString
}

object State {
  case object OK extends State
  case object UNAVAILABLE extends State
  case object UNKNOWN extends State

  val values: Seq[State] = Seq(OK, UNAVAILABLE, UNKNOWN)

  implicit val format: Format[State] = new Format[State] {

    override def writes(o: State): JsValue = JsString(o.value)

    override def reads(json: JsValue): JsResult[State] =
      json.validate[String].flatMap {
        case OK.value          => JsSuccess(OK)
        case UNAVAILABLE.value => JsSuccess(UNAVAILABLE)
        case UNKNOWN.value     => JsSuccess(UNKNOWN)
        case e                 => JsError(s"invalid value: $e for State type")
      }
  }
}

case class Status(state: Option[State], lastUpdated: Option[Instant])

object Status {
  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat
  implicit val format:        OFormat[Status] = Json.using[Json.WithDefaultValues].format[Status]
}

case class CustomsServiceStatus(name: String, status: Status, description: String)

object CustomsServiceStatus {
  implicit val format: OFormat[CustomsServiceStatus] = Json.format[CustomsServiceStatus]
}

case class CustomsServiceStatusAll(services: List[CustomsServiceStatus])

object CustomsServiceStatusAll {
  implicit val format: OFormat[CustomsServiceStatusAll] = Json.format[CustomsServiceStatusAll]
}