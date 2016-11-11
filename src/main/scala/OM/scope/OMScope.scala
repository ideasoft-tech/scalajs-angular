package OM.scope

import OM.utils.Datatypes.{ParameterInput, StoreModel}
import OM.utils.Panel
import com.greencatsoft.angularjs.core.Scope

import scala.scalajs.js

/**
  * Created by jvelazquez on 11/4/2016.
  */
@js.native
trait OMScope extends Scope {

  var name: String = js.native

  var host: String = js.native

  var searchFilter: String = js.native

  var method: String = js.native

  var urlInput: String = js.native

  var resultPanel: js.Array[Panel] = js.native

  var parameters: js.Array[ParameterInput] = js.native

  var store: js.Array[StoreModel] = js.native

  var itemActive: StoreModel = js.native

}
