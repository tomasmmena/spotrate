package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test._
import play.api.test.Helpers._
import play.api.libs.json.Json
import scala.xml.XML.loadString

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 *
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class APIControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "APIController POST" should {

    "return correct value formatted as json" in {
      val controller = new APIController(stubControllerComponents())
      val jsonResponse = controller.json().apply(FakeRequest(POST, "/api/json").withJsonBody(Json.parse(
      s"""{
          |  "start": "2018-05-06 11:30:52",
          |  "end": "2018-05-06 15:00:03"
          |}""".stripMargin)))

      status(jsonResponse) mustBe OK
      //contentType(jsonResponse) mustBe Some(AcceptExtractors.Accepts.Json.mimeType)
      contentAsJson(jsonResponse) mustEqual (Json.parse(
        s"""{
            |  "rate": 2000
            |}""".stripMargin
      ))
    }

    "return correct value formatted as xml" in {
      val controller = new APIController(stubControllerComponents())
      val xmlResponse = controller.xml().apply(FakeRequest(POST, "/api/xml").withXmlBody(
        <request>
            <start>2018-05-06 11:30:52</start>
            <end>2018-05-06 15:00:03</end>
        </request>
      ))

      status(xmlResponse) mustBe OK
      //contentType(jsonResponse) mustBe Some(AcceptExtractors.Accepts.Json.mimeType)
      loadString(contentAsString(xmlResponse)) mustEqual (
<response>
    <rate>2000.0</rate>
</response>
      )
    }
  }
    
}
