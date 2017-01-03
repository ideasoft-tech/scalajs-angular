package OM.model

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll
import scala.util.Try

/**
  * Created by jvelazquez on 12/26/2016.
  */

@JSExportAll
object RequestBind {
  val HEADER = "HEADER"
  val PARAMETER = "PARAMETER"
  val BODY = "BODY"
}


@JSExportAll
abstract class UIModel(var label: String, var key: String, var requestBind: String) {
  var value: String = ""

  def getValue = value

  def getLabel = label

  def uiType(): String

  def isBody: Boolean = requestBind == RequestBind.BODY

  def isParameter: Boolean = requestBind == RequestBind.PARAMETER

  def isHeader: Boolean = requestBind == RequestBind.HEADER
}


@JSExportAll
class Textfield(label: String, key: String, requestBind: String)
  extends UIModel(label, key, requestBind) {

  override def uiType(): String = "textfield"

}


@JSExportAll
class Description(label: String, key: String, requestBind: String)
  extends UIModel(label, key, requestBind) {

  override def uiType(): String = "description"
}


@JSExportAll
class Combobox(label: String, key: String, requestBind: String, var values: js.Array[js.Tuple2[String, String]])
  extends UIModel(label, key, requestBind) {

  override def uiType(): String = "combobox"
}


@JSExportAll
class File(label: String, key: String, requestBind: String)
  extends UIModel(label, key, requestBind) {

  override def uiType(): String = "file"

}

@JSExportAll
class PayloadXML(label: String, key: String, requestBind: String, startTag: String, endTag: String, model: js.Array[UIModel])
  extends UIModel(label, key, requestBind) {

  override def uiType(): String = "payloadXML"

  def getTemplate = "%s%s\n%s".format(startTag, model.foldLeft("")((ac, p) => ac + s"\n\t<${p.key}>${p.value}</${p.key}>"), endTag)

  override def getValue: String = getTemplate

  def getUIModel = model
}

@JSExportAll
class Panel(label: String, var content: js.Array[UIModel])
  extends UIModel(label, null, null) {
  def uiType(): String = "panel"

  def find(f: (UIModel) => Boolean): js.Array[Option[UIModel]] = content.flatMap({
    case t: Panel => t.find(f)
    case t: UIModel => js.Array(if (f(t)) Some(t) else None)
  })

  def filter(f: (UIModel) => Boolean): js.Array[UIModel] = content.flatMap({
    case t: Panel => t.filter(f)
    case t: UIModel => if (f(t)) js.Array(t) else Nil
  })


}

@JSExportAll
class HeaderPanel(v: js.Dictionary[String]) extends UIModel("Headers", null, null) {

  var hash: js.Dictionary[String] = v
  private var counter: Int = 0

  override def uiType(): String = "header-panel"

  def getHash = hash

  def addRow() = {
    hash.put(s"name$counter", "")
    counter += 1
  }

  def removeKey(key: String) = {
    hash.delete(key)
  }
}

@JSExportAll
class OMModel(
               var name: String,
               var url: String,
               var method: String,
               var soapName: String,
               var definitionPath: String,
               var description: String,
               var requiredFields: js.Array[UIModel]
             ) {

  def getID = soapName

  def getFixedHeaders = requiredFields.flatMap({
    case t: HeaderPanel => t.getHash
    case t: Panel => t.filter(fixedHeaders).flatMap {
      case t: HeaderPanel => t.getHash
    }.toMap
  }).toMap

  def getPayloadComponent = {

    val result = requiredFields.flatMap({
      case p: Panel => p.find(bodyFilter).flatten
      case p: UIModel => if (bodyFilter(p)) Some(p) else None
    })

    Try(result(0))
  }

  def getUrlParameters = requiredFields.flatMap({
    case t: Panel => t.filter(parameterFilter)
    case t: UIModel => if (parameterFilter(t)) js.Array(t) else Nil
  })

  def getHeaders = requiredFields.flatMap({
    case t: Panel => t.filter(headersFilter)
    case t: UIModel => if (headersFilter(t)) js.Array(t) else Nil
  }).map(m => m.key -> m.value).toMap

  private def fixedHeaders(p: UIModel) = p match {
    case t: HeaderPanel => true
    case _ => false
  }

  private def parameterFilter(p: UIModel) = p.isParameter

  private def headersFilter(p: UIModel) = p.isHeader

  private def bodyFilter(p: UIModel) = p.isBody
}

