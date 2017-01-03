package OM.controller

import OM.JSON
import OM.model._
import OM.scope.OMScope
import OM.services.OMServices
import OM.spec.EnviromentSpec
import com.greencatsoft.angularjs.{AbstractController, injectable}
import org.scalajs.dom.raw.HTMLInputElement
import org.scalajs.dom.{Event, FileReader, UIEvent}

import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichGenMap
import scala.scalajs.js.annotation.JSExport

/**
  * Created by jvelazquez on 11/4/2016.
  */


@injectable("userDetailsCtrl")
class OMController(scope: OMScope, omServices: OMServices) extends AbstractController[OMScope](scope) {

  override def initialize(): Unit = {
    super.initialize()

    scope.services = EnviromentSpec.spec
    scope.serviceID = scope.services.head.soapName
    scope.serviceSelected = scope.services.head
    scope.templateURL = "./views/builder.html"
    scope.menuSpec = TreeMenuBuilder.build(scope.services)
    scope.context = EnviromentSpec.context
    scope.groupSelected = scope.context.getGroups(0)
    scope.envSelected = scope.context.getEnvs(scope.groupSelected)(0)
    scope.showResult = false
    scope.activeLoader = false
    scope.conextMap = scope.context.getGroups.map(g => g -> scope.context.getEnvs(g)).toMap.toJSDictionary
  }

  @JSExport
  def serviceChange() = {
    scope.serviceSelected = scope.services.find(p => p.getID == scope.serviceID).orNull

    val scopeSelection = scope.serviceID
    scope.templateURL = s"./views/builder.html?$scopeSelection"
  }

  @JSExport
  def setService(servicesID: String) = {

    scope.showResult = false
    scope.serviceID = servicesID
    scope.serviceSelected = scope.services.find(p => p.getID == scope.serviceID).orNull

    scope.templateURL = s"./views/builder.html?${math.random}"

  }


  @JSExport
  def setFiles(element: Event) = {

    val value = element.target.asInstanceOf[HTMLInputElement].value
    val payloadComponent = scope.serviceSelected.getPayloadComponent

    if (payloadComponent.isSuccess) {

      payloadComponent.get.value = value
      // para leer archivo desde JS
      val reader = new FileReader()

      reader.onload = (e: UIEvent) => {
        payloadComponent.get.value = reader.result.asInstanceOf[String]
      }

      reader.readAsText(element.target.asInstanceOf[HTMLInputElement].files(0))
    }
  }

  @JSExport
  def setGroup(group: String) = {
    scope.groupSelected = group
    println(group)
  }

  @JSExport
  def setEnv(group: String, env: String) = {
    scope.envSelected = env
    scope.groupSelected = group
    println(s"$group  $env")
  }

  @JSExport
  def submitRequest() = {

    scope.activeLoader = true

    scope.detailsResult = JSON.stringify(
      omServices.submitRequest(scope, successInvoke, errorCallback),
      null,
      4
    )
  }

  def successInvoke(data: Any, status: Int) = {

    scope.showResult = true
    scope.plainResult = JSON.stringify(data.asInstanceOf[js.Any])
    scope.formatResult = JSON.stringify(data.asInstanceOf[js.Any], null, 4)
    scope.activeLoader = false

  }

  def errorCallback(d: Any, status: Int) = {
    val obj = d.asInstanceOf[js.Dynamic]
    successInvoke(obj, status)
  }

}