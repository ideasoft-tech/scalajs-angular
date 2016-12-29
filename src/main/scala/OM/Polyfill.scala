package OM

import com.greencatsoft.angularjs.core.{HttpConfig, HttpPromise, HttpService}
import com.greencatsoft.angularjs.injectable

import scala.scalajs.js
import scala.scalajs.js._

/**
  * Created by jvelazquez on 12/27/2016.
  */
@js.native
@injectable("$http")
class OMHttpService extends HttpService {
  def patch[T](url: String, data: js.Any, config: HttpConfig): HttpPromise[T] = js.native
}

@js.native
trait OMHttpConfig extends HttpConfig {
  var data: js.Any
}

@native
object JSON extends Object {
  /**
    * Parse a string as JSON, optionally transforming the value produced by parsing.
    * @param text The string to parse as JSON.  See the JSON object for a
    *             description of JSON syntax.
    * @param reviver If a function, prescribes how the value originally produced
    *                by parsing is transformed, before being returned.
    *
    * MDN
    */
  def parse(text: String, reviver: Function2[Any, Any, Any] = ???): Dynamic = native

  /**
    * Convert a value to JSON, optionally replacing values if a replacer function
    * is specified, or optionally including only the specified properties if a
    * replacer array is specified.
    *
    * @param value The value to convert to a JSON string.
    * @param replacer If a function, transforms values and properties encountered
    *                 while stringifying; if an array, specifies the set of
    *                 properties included in objects in the final string.
    * @param space Causes the resulting string to be pretty-printed.
    *
    * MDN
    */
  def stringify(value: Any, replacer: Function2[String, Any, Any] = ???, space: Any = ???): String = native
//  def stringify(value: Any, replacer: Array[Any]): String = native
//  def stringify(value: Any, replacer: Array[Any], space: Any): String = native
  def stringify(value: Any, replacer: Any, space: Any): String = native
}
