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

package uk.gov.hmrc.customsservicestatusfrontend.utils

import play.api.i18n.Messages

import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneId}
import java.util.Locale

object Formatters {

  private def zoneId = ZoneId.of("Europe/London")

  def instantFormatDate(instant: Instant): String =
    DateTimeFormatter
      .ofPattern("dd MMMM yyyy")
      .withZone(zoneId)
      .format(instant)

  def instantFormatHours(instant: Instant)(implicit messages: Messages): String = hoursFormatter(messages.lang.locale).format(instant)

  def hoursFormatter(locale: Locale): DateTimeFormatter =
    DateTimeFormatter
      .ofPattern("h:mma z")
      .withLocale(locale)
      .withZone(zoneId)
}
