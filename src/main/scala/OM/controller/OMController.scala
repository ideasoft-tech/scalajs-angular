package OM.controller

import OM.model._
import OM.scope.OMScope
import OM.utils.Panel
import OM.{JSON, OMHttpConfig, OMHttpService}
import com.greencatsoft.angularjs._
import org.scalajs.dom.raw.HTMLInputElement
import org.scalajs.dom.{Event, FileReader, UIEvent}
import upickle._

import scala.scalajs.js
import scala.scalajs.js.JSConverters.{JSRichGenMap, JSRichGenTraversableOnce}
import scala.scalajs.js.annotation.JSExport

/**
  * Created by jvelazquez on 11/4/2016.
  */


@injectable("userDetailsCtrl")
class OMController(scope: OMScope, http: OMHttpService) extends AbstractController[OMScope](scope) {

  lazy val parametersRegex = """{[a-z|A-Z|0-9]+}""".r
  val storageKey = "OM-History-Rest"


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

    val scopeSelection = scope.serviceID
    scope.templateURL = s"./views/builder.html?$scopeSelection"
    println(scope.templateURL)
  }


  @JSExport
  def setFiles(element: Event) = {

    val value = element.target.asInstanceOf[HTMLInputElement].value
    val payloadComponent = scope.serviceSelected.getPayloadComponent

    if (payloadComponent.isSuccess) {

      payloadComponent.get.value = value

      val reader = new FileReader()

      reader.onload = (e: UIEvent) => {
        payloadComponent.get.value = reader.result.asInstanceOf[String]
      }

      reader.readAsText(element.target.asInstanceOf[HTMLInputElement].files(0))
    }
  }

  @JSExport
  def addHeaderRow(): Unit = {
    scope.serviceSelected.headers.push(new HeaderModel(null, null))
  }

  @JSExport
  def removeHeaderRow(index: Int): Unit = {
    scope.serviceSelected.headers.splice(index, 1);
  }

  @JSExport
  def setEnv(env: String) = {
    scope.envSelected = env

  }

  @JSExport
  def submitRequest() = {
    scope.activeLoader = true

    val httpConfig = buildRestCall(scope.serviceSelected)

    scope.detailsResult = JSON.stringify(httpConfig, null, 4)

    httpConfig.method match {
      case "GET" =>
        http.get(httpConfig.url, httpConfig)
          .success(successInvoke)
          .error(errorCallback)

      case "POST" =>
        http.post(httpConfig.url, httpConfig.data, httpConfig)
          .success(successInvoke)
          .error(errorCallback)

      case "PATCH" =>
        http.patch(httpConfig.url, httpConfig.data, httpConfig)
          .success(successInvoke)
          .error(errorCallback)

      case "PUT" =>
        http.put(httpConfig.url, httpConfig.data, httpConfig)
          .success(successInvoke)
          .error(errorCallback)
    }
  }

  private def buildRestCall(oMModel: OMModel): OMHttpConfig = {

    if (scope.envSelected == null) {
      scope.envSelected = scope.context.getEnvs(scope.groupSelected)(0)
    }


    val httpConfig: OMHttpConfig = new js.Object().asInstanceOf[OMHttpConfig]

    // AMBIENTE
    val env = scope.context.setCurrent(scope.groupSelected, scope.envSelected)

    val uiModel = scope.serviceSelected

    // obtengo datos del modelo

    val fixedHeaders = oMModel.getFixedHeaders
    val headers = oMModel.getFixedHeaders
    val urlParameters = oMModel.getUrlParameters
    val payload = oMModel.getPayloadComponent

    // GENERO URL
    val host = env.getVariableValue(scope.groupSelected, "host")

    val tmpUrl = urlParameters.foldLeft(uiModel.url)((acc: String, p: UIModel) => acc.replaceAll(s"{${p.key}}", p.value))

    httpConfig.url = s"$host/$tmpUrl"
    httpConfig.method = oMModel.method
    httpConfig.headers = (fixedHeaders ++ headers).toJSDictionary

    if (payload.isSuccess) {
      httpConfig.data = payload.get.getValue
    }

    httpConfig
  }

  def successInvoke = (data: js.Any, status: Int) => {

    scope.showResult = true

    scope.plainResult = JSON.stringify(data)
    scope.formatResult = JSON.stringify(data, null, 4)

    scope.activeLoader = false
  }


  def errorCallback: js.Function2[Any, Int, Unit] = (d: Any, status: Int) => {
    val obj = d.asInstanceOf[js.Dynamic]
    successInvoke(obj, status)
  }


  def checkOnlyObj(value: Js.Value): Boolean = value match {
    case x: Js.Obj =>
      x.obj.exists(p => {
        p._2.isInstanceOf[Js.Str] || p._2.isInstanceOf[Js.Num]
      })

    case _ => false
  }

  def buildTable(value: Js.Value): List[Panel] = {

    val objValue = value.obj

    if (checkOnlyObj(value)) {
      val panel = new Panel

      panel.isPanel = true
      panel.isNestedList = true

      panel.nestedList = objValue.map(f => {
        (f._1, f._2.value.toString)
      }).toJSDictionary

      List(panel)

    } else {

      objValue.map(pair => {
        //        primer nivel del objeto
        val name = pair._1
        val value = pair._2

        val panel = new Panel

        panel.name = name

        // segundo nivel, anidamiento de panel
        value match {
          case v: Js.Arr =>

            panel.isNestedTable = true

            val vArr = v.arr

            if (vArr.nonEmpty && checkOnlyObj(vArr.head)) {

              panel.nestedTable = vArr.map(f => {

                f.obj.map(h => (h._1, h._2.str)).toJSDictionary

              }).toJSArray


            }
          case v: Js.Obj =>
            // tercer nivel
            panel.isNestedPanels = true
            panel.nestedPanels = v.obj.map(f => {

              val p = new Panel

              p.name = f._1
              p.nestedList = f._2.obj.map(fPair => (fPair._1, fPair._2.str)).toJSDictionary

              p

            }).toJSArray
          case _ => Nil
        }
        panel
      }).toList
    }
  }


}