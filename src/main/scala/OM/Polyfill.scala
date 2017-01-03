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

  def parse(text: String, reviver: Function2[Any, Any, Any] = ???): Dynamic = native

  def stringify(value: Any): String = native

  def stringify(value: Any, replacer: Any, space: Any): String = native
}
