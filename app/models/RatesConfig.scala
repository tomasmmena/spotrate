package models

import scala.io.Source
import play.api.libs.json._

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}


/**
 * This class handles loading a configuration file and parsing it. It also implements a method to 
 * retrieve a rate from the resulting structure.
 */
class RatesConfig(val filePath: String) {
	var cache: Option[JsValue] = None

	/**
	 * This method reads and parses the file if it is not loaded to memory yet and returns the json 
	 * structure.
	 */
	def getData(): JsValue = {
		if (!cache.isDefined) {
			cache = Option(Json.parse(Source.fromFile(this.filePath).getLines.mkString("\n")))
		}
		cache match {
			case Some(value) => value
			case None => Json.parse("{}")
		}
	}

	/**
	 * This method returns a rate as a float given a starting and ending datetime. If a rate is not 
	 * available it returns -1 structure.
	 */
	def findRate(start: DateTime, end: DateTime): Float = {
		if (start.getYear == end.getYear && start.getDayOfYear == end.getDayOfYear && start.isBefore(end)) {
			val dayOfWeek: String = start.getDayOfWeek match {
				case 1 => "mon"
				case 2 => "tues"
				case 3 => "wed"
				case 4 => "thurs"
				case 5 => "fri"
				case 6 => "sat"
				case 7 => "sun"
			}
			val timeFormat: DateTimeFormatter = DateTimeFormat.forPattern("HHmm")

			for (rate <- this.getData()("rates").as[List[JsValue]]) {
				if (rate("days").as[String].contains(dayOfWeek)) {
					val times: String = rate("times").as[String]
					val startTime = times.slice(0, 4)
					val endTime = times.slice(5, 9)
					if (startTime <= timeFormat.print(start) && endTime >= timeFormat.print(end)) {
						return rate("price").as[Float]
					}
				}
			}
			return -1f
		}
		-1f
	}
}