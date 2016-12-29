package OM.scope

import OM.model.ApiClient.BasicEnvironment
import OM.model.{OMModel, TreeMenu}
import com.greencatsoft.angularjs.core.Scope

import scala.scalajs.js

/**
  * Created by jvelazquez on 11/4/2016.
  */
@js.native
trait OMScope extends Scope {

  // contexto
  var context: BasicEnvironment = js.native
  var groupSelected: String = js.native
  var envSelected: String = js.native

  // para manejar los distintos templates
  var templateURL: String = js.native

  // SERVICIO
  var services: js.Array[OMModel] = js.native
  var serviceSelected: OMModel = js.native
  var serviceID: String = js.native

  //  var serviceModel: js.Array[UIModel] = js.native

  // MENU
  var menuSpec: TreeMenu = js.native

  // RESULTADOS
  var showResult: Boolean = js.native
  var activeLoader: Boolean = js.native

  var plainResult: String = js.native
  var formatResult: String = js.native
  var detailsResult: String = js.native

}
