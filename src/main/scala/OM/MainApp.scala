package OM

import OM.controller.OMController
import OM.utils.OMServicesFactory
import com.greencatsoft.angularjs.Angular

import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

/**
  * Created by jvelazquez on 11/3/2016.
  */
object MainApp extends JSApp {

  @JSExport
  def main(): Unit = {

    val mod = Angular.module("om-app")

    mod.controller[OMController]
    mod.factory[OMServicesFactory]
  }

}


