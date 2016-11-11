package OM.utils

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

/**
  * Created by jvelazquez on 11/4/2016.
  */
@JSExportAll
case class Panel() {

  var name: String = _

  var nestedTable: js.Array[js.Dictionary[String]] = _

  var nestedList: js.Dictionary[String] = _

  var nestedPanels: js.Array[Panel] = _

  var isPanel = false

  var isNestedList = false

  var isNestedTable = false

  var isNestedPanels = false
}
