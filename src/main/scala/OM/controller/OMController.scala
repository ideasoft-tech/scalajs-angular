package OM.controller

import OM.scope.OMScope
import OM.utils.Datatypes.{ParameterInput, StoreModel}
import OM.utils.{Datatypes, Panel}
import com.greencatsoft.angularjs.core.{HttpConfig, HttpService}
import com.greencatsoft.angularjs.{AbstractController, injectable}
import org.scalajs.dom
import upickle._

import scala.scalajs.js
import scala.scalajs.js.JSConverters.{JSRichGenMap, JSRichGenTraversableOnce}
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport

/**
  * Created by jvelazquez on 11/4/2016.
  */
@JSExport
@injectable("userDetailsCtrl")
class OMController(scope: OMScope, http: HttpService) extends AbstractController[OMScope](scope) {

  lazy val parametersRegex = """{[a-z|A-Z|0-9]+}""".r
  val storageKey = "OM-History-Rest"

  override def initialize(): Unit = {
    super.initialize()

    scope.name = ""
    scope.searchFilter = ""
    scope.host = "http://192.168.10.110:8989"
    scope.urlInput = ""
    scope.method = "GET"
    scope.parameters = js.Array()
    scope.resultPanel = js.Array()
    scope.store = js.Array()

    val value = dom.window.localStorage.getItem(storageKey)

    if (value != null) {
      scope.store = json.read(value).arr.map(x => Datatypes.toStoreModel(x)).toJSArray

      if (scope.store.nonEmpty) {
        updateScope(scope.store(0))
      }
    }
  }

  @JSExport
  def urlChange(newValue: String) = {
    val newParams = parametersRegex.findAllMatchIn(newValue).map(f => {

      val filter = scope.parameters.filter(x => x.name == f.toString)

      if (filter.isEmpty) {
        new ParameterInput(f.toString, "")
      } else {
        filter.head
      }
    }
    ).toJSArray

    scope.parameters = newParams
  }


  @JSExport
  def removeSavedItem(storeModel: StoreModel) = {

    scope.store = scope.store.filterNot(x => x.url == storeModel.url)

    dom.window.localStorage.setItem(storageKey, JSON.stringify(scope.store))
  }

  @JSExport
  def saveOnStore() = {

    val newStore = new StoreModel(
      scope.host,
      scope.method,
      scope.urlInput,
      scope.parameters
    )

    scope.store += newStore

    dom.window.localStorage.setItem(storageKey, JSON.stringify(scope.store))
  }

  @JSExport
  def savedItemClick(storeModel: StoreModel) = {
    if (scope.itemActive != null) {
      scope.itemActive.active = false
    }

    updateScope(storeModel)
  }


  @JSExport
  def submitRequest() = {

    val finalUrl = scope.parameters.foldLeft(scope.urlInput)(
      (accum: String, p: ParameterInput) => {
        accum.replace(p.name, p.value toString)
      }
    )

    invokeService(scope.host + finalUrl)
    //    invokeService("dataTest.json")
  }

  private def updateScope(storeModel: StoreModel) = {

    storeModel.active = true

    scope.host = storeModel.host
    scope.method = storeModel.method
    scope.urlInput = storeModel.url
    scope.parameters = storeModel.parameterInput

    scope.itemActive = storeModel

  }

  private def invokeService(url: String) = scope.method match {

    case "GET" => http.get(url).
      success(successInvoke).
      error(errorCallback)

    case "POST" => http.post(url).
      success(successInvoke).
      error(errorCallback)

    case _ =>
      val httpConfig = HttpConfig.empty
      httpConfig.headers = js.Dictionary(
        "method" -> "PATH"
      )

      http.head(url, httpConfig).
        success(successInvoke).
        error(errorCallback)
  }


  def successInvoke = (data: Any, status: Int) => {

    try {

      data match {
        case str: String =>

          val a = json.read(str)
          scope.resultPanel = buildTable(a) toJSArray

        case _ =>

          val dyn = data.asInstanceOf[js.Dynamic]
          val a = json.read(JSON.stringify(dyn))

          scope.resultPanel = buildTable(a) toJSArray
      }

    } catch {
      case e: Exception => println(e.getMessage)
    }
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
        (f._1, f._2.value toString)
      }) toJSDictionary

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

                f.obj.map(h => (h._1, h._2.str)) toJSDictionary

              }) toJSArray


            }
          case v: Js.Obj =>
            // tercer nivel
            panel.isNestedPanels = true
            panel.nestedPanels = v.obj.map(f => {

              val p = new Panel

              p.name = f._1
              p.nestedList = f._2.obj.map(fPair => (fPair._1, fPair._2.str)) toJSDictionary

              p

            }) toJSArray
          case _ => Nil
        }
        panel
      }) toList
    }
  }


}