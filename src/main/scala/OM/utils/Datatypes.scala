package OM.utils

import upickle.Js

import scala.scalajs.js
import scala.scalajs.js.JSConverters.genTravConvertible2JSRichGenTrav
import scala.scalajs.js.annotation.ScalaJSDefined

/**
  * Created by jvelazquez on 11/4/2016.
  */


object Datatypes {


  @ScalaJSDefined
  class StoreModel(
                    var host: String,
                    var method: String,
                    var url: String,
                    var parameterInput: js.Array[ParameterInput],
                    var active: Boolean = false) extends js.Object


  @ScalaJSDefined
  class ParameterInput(
                        var name: String,
                        var value: String
                      ) extends js.Object


  def toParameterInput(v: Js.Value): ParameterInput = {
    val o = v.obj
    new ParameterInput(o("name").str, o("value").str)
  }

  def toStoreModel(v: Js.Value): StoreModel = {

    val o = v.obj

    new StoreModel(
      o("host").str,
      o("method").str,
      o("url").str,
      o("parameterInput").arr.map(x => toParameterInput(x)).toJSArray
    )
  }

}



