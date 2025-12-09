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

package uk.gov.hmrc.customsservicestatusfrontend.views

import org.jsoup.nodes.Document
import org.scalatest.Assertion
import play.api.i18n.Messages
import play.twirl.api.Html
import uk.gov.hmrc.customsservicestatusfrontend.helpers.BaseViewSpec

class ViewBehaviours extends BaseViewSpec {

  def normalPage(headingKey: String, headingArgs: Seq[String] = Seq(), section: Option[String] = None)(html: Html): Unit =
    "behave like a normal page" when {

      val document = html.asDocument

      "rendered" should {

        "display govuk header content" in {
          document.getElementsByClass("govuk-header__link govuk-header__service-name").text() shouldBe messages("service.name")
        }

        "display the correct browser title" in {
          assertEqualsMessage(document, "title", title(Messages(headingKey, headingArgs*), section))
        }

        "display the correct page heading" in {
          assertPageHeadingEqualsMessage(document, headingKey, headingArgs*)
        }
      }
    }

  private def assertPageHeadingEqualsMessage(doc: Document, expectedMessageKey: String, args: Any*): Assertion = {
    val pageHeading = Messages(expectedMessageKey, args*).replaceAll("&nbsp;", " ")
    val headers     = doc.getElementsByTag("h1")
    if (headers.isEmpty)
      doc.body().getElementsContainingOwnText(pageHeading).size shouldBe 1
    else {
      headers.size                               shouldBe 1
      headers.first.text.replaceAll("\u00a0", " ") should include(pageHeading)
    }
  }

  private def assertEqualsMessage(doc: Document, cssSelector: String, expectedMessageKey: String): Assertion =
    assertEqualsValue(doc, cssSelector, Messages(expectedMessageKey))

  private def assertEqualsValue(doc: Document, cssSelector: String, expectedValue: String): Assertion = {
    val elements = doc.select(cssSelector)

    if (elements.isEmpty) throw new IllegalArgumentException(s"CSS Selector $cssSelector wasn't rendered.")

    // <p> HTML elements are rendered out with a carriage return on some pages, so discount for comparison
    assert(elements.first().html().replace("\n", "") === expectedValue)
  }

  def title(heading: String, section: Option[String] = None)(implicit messages: Messages): String =
    section match {
      case Some(section) => s"$heading - $section - ${messages("service.name")} - ${messages("service.title.suffix")}"
      case None          => s"$heading - ${messages("service.name")} - ${messages("service.title.suffix")}"
    }

  def pageWithoutBackLink(html: Html): Unit =
    "behave like a page without a 'Back' link " when {
      "a 'Back' link is not displayed as expected" in {
        html.asDocument.getElementsByClass("govuk-back-link").size() shouldBe 0
      }
    }

  def pageWithPageNotWorkingLink(html: Html): Unit =
    "behave like a page with a 'Page not working' link " when {
      "the link is displayed as expected" in {
        html.asDocument
          .getElementsByClass("govuk-link hmrc-report-technical-issue ")
          .attr("href")
          .contains("http://localhost:9250/contact/report-technical-problem?service=customs-service-status-frontend&amp;referrerUrl=%2F")

        html.asDocument
          .getElementsByClass("govuk-link hmrc-report-technical-issue ")
          .text() shouldBe "Is this page not working properly? (opens in new tab)"
      }
    }

}
