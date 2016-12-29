package OM.model

import scala.scalajs.js.annotation.JSExportAll

/**
  * Created by jvelazquez on 12/28/2016.
  */
@JSExportAll
class HeaderModel(var key: String, var value: String) {

  def isValid: Boolean = key != null && value != null

}
