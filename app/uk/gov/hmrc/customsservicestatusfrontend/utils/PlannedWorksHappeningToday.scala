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

package uk.gov.hmrc.customsservicestatusfrontend.utils

import uk.gov.hmrc.customsservicestatusfrontend.models.OutageData

import java.time.{LocalDate, ZoneId}

class PlannedWorksHappeningToday {

  private val today: LocalDate = LocalDate.now(ZoneId.of("Europe/London"))

  def plannedWorksHappeningToday(plannedOutageData: List[OutageData]): List[OutageData] = plannedOutageData.filter { outage =>
    val start = outage.startDateTime.atZone(ZoneId.of("Europe/London")).toLocalDate
    outage.endDateTime match {
      case Some(endDate) =>
        val end = endDate.atZone(ZoneId.of("Europe/London")).toLocalDate
        !(today.isBefore(start) || today.isAfter(end))
      case None =>
        today.isEqual(start)
    }
  }

}
