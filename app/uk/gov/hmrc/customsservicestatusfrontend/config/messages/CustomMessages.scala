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

package uk.gov.hmrc.customsservicestatusfrontend.config.messages

import play.api.i18n.{Lang, Messages, MessagesApi}

case class CustomMessages(lang: Lang, messagesApi: MessagesApi) extends Messages {

  def english(key: String, args: Any*): String =
    messagesApi(key, args*)(Lang("en"))

  override def apply(key: String, args: Any*): String =
    messagesApi(key, args*)(lang)

  override def apply(keys: Seq[String], args: Any*): String =
    messagesApi(keys, args*)(lang)

  override def translate(key: String, args: Seq[Any]): Option[String] =
    messagesApi.translate(key, args)(lang)

  override def isDefinedAt(key: String): Boolean =
    messagesApi.isDefinedAt(key)(lang)

  override def asJava: play.i18n.Messages =
    new play.i18n.MessagesImpl(lang.asJava, messagesApi.asJava)
}
