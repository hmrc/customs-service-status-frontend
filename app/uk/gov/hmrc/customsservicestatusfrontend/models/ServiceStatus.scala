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

import play.api.libs.json.*
import play.api.libs.json.Json.WithDefaultValues
import uk.gov.hmrc.customsservicestatusfrontend.utils.EqualUtils.AnyOps

import java.time.Instant

enum State {
  case Available
  case Unavailable
  case Unknown

  val value: String = toString
}

object State {

  def apply(value: String): Option[State] =
    values.find(_.value === value)

  def unapply(state: State): String =
    state.value

  implicit val format: Format[State] = new Format[State] {

    override def reads(json: JsValue): JsResult[State] =
      try json.validate[String] map State.valueOf
      catch case e: IllegalArgumentException => JsError("Invalid State")

    override def writes(o: State): JsValue = JsString(o.value)
  }
}

case class CustomsServiceStatus(
  id:             String,
  name:           String,
  description:    String,
  state:          Option[State],
  stateChangedAt: Option[Instant],
  lastUpdated:    Option[Instant]
)

object CustomsServiceStatus {
  implicit val format: OFormat[CustomsServiceStatus] = Json.using[WithDefaultValues].format[CustomsServiceStatus]
}

case class ServiceStatuses(services: List[CustomsServiceStatus])

object ServiceStatuses {
  implicit val format: OFormat[ServiceStatuses] = Json.format[ServiceStatuses]
}
