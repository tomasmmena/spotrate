package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import play.api.libs.json._
import play.api.libs.functional.syntax._

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import models.RatesConfig

/**
 * This controller creates manages the spotrate API.
 */
@Singleton
class APIController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  /**
   * Process request using json.
   */
  def json() = Action { request: Request[AnyContent] => 
    val rates = new RatesConfig("C:\\Users\\tomas\\IdeaProjects\\spotrate2\\spotrate\\conf\\rates.json")
    
    val dateFormat = "yyyy-MM-dd HH:mm:ss"

    request.body.asJson.map {
      (json: JsValue) => {
        val startDateTime: DateTime = DateTime.parse((json \ "start").as[String], DateTimeFormat.forPattern(dateFormat))
        val endDateTime: DateTime = DateTime.parse((json \ "end").as[String], DateTimeFormat.forPattern(dateFormat))

        val rate = rates.findRate(startDateTime, endDateTime)
        Ok(rate match {
          case -1f => Json.obj("error" -> "Rate unavailable.")
          case _ => Json.obj("rate" -> rate)
        })
      }
    }.getOrElse {
      BadRequest("Expecting Json data")
    }
  }

  /**
   * Process request using xml.
   */
  def xml() = Action { request: Request[AnyContent] => 
    val rates = new RatesConfig("C:\\Users\\tomas\\IdeaProjects\\spotrate2\\spotrate\\conf\\rates.json")
    
    val dateFormat = "yyyy-MM-dd HH:mm:ss"

    request.body.asXml.map {
      (xml: scala.xml.NodeSeq) => {
        val startDateTime: DateTime = DateTime.parse((xml \ "start").text, DateTimeFormat.forPattern(dateFormat))
        val endDateTime: DateTime = DateTime.parse((xml \ "end").text, DateTimeFormat.forPattern(dateFormat))

        //println(startDateTime)
        val rate = rates.findRate(startDateTime, endDateTime)
        Ok(rate match {
          case -1f => <response>
    <error>Rate unavailable.</error>
</response>
          case _ => <response>
    <rate>{rate}</rate>
</response>
        }

        )
      }
    }.getOrElse {
      BadRequest("Expecting Json data")
    }
  }

}
