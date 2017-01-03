package OM.services

import OM.model.UIModel
import OM.scope.OMScope
import OM.{JSON, OMHttpConfig, OMHttpService}
import com.greencatsoft.angularjs.core.HttpConfig
import com.greencatsoft.angularjs.injectable

import scala.scalajs.js
import scala.scalajs.js.JSConverters.JSRichGenMap

/**
  * Created by jvelazquez on 1/3/2017.
  */
@injectable("omServices")
class OMServices(http: OMHttpService) {

  def buildRestCall(scope: OMScope): OMHttpConfig = {

    val omModel = scope.serviceSelected
    val groupSelected = scope.groupSelected
    val envSelected = if (scope.envSelected == null) scope.context.getEnvs(groupSelected)(0) else scope.envSelected
    // AMBIENTE
    val env = scope.context.setCurrent(groupSelected, envSelected)

    val httpConfig: OMHttpConfig = new js.Object().asInstanceOf[OMHttpConfig]

    val uiModel = scope.serviceSelected

    // obtengo datos del modelo

    val fixedHeaders = omModel.getFixedHeaders
    val headers = omModel.getHeaders
    println(JSON.stringify(headers.toJSDictionary))
    val urlParameters = omModel.getUrlParameters
    val payload = omModel.getPayloadComponent

    // GENERO URL
    val host = env.getVariableValue(groupSelected, "host")

    val tmpUrl = urlParameters.foldLeft(uiModel.url)((acc: String, p: UIModel) => acc.replaceAll(s"{${p.key}}", p.value))

    httpConfig.url = s"$host/$tmpUrl"
    httpConfig.method = omModel.method
    httpConfig.headers = (fixedHeaders ++ headers).toJSDictionary

    if (payload.isSuccess) {
      httpConfig.data = payload.get.getValue
    }

    httpConfig
  }


  def submitRequest(scope: OMScope, succ: (Any, Int) => Unit, err: (Any, Int) => Unit): HttpConfig = {

    val httpConfig = buildRestCall(scope)

    httpConfig.method match {
      case "GET" =>
        http.get(httpConfig.url, httpConfig)
          .success(succ)
          .error(err)

      case "POST" =>
        http.post(httpConfig.url, httpConfig.data, httpConfig)
          .success(succ)
          .error(err)

      case "PATCH" =>
        http.patch(httpConfig.url, httpConfig.data, httpConfig)
          .success(succ)
          .error(err)

      case "PUT" =>
        http.put(httpConfig.url, httpConfig.data, httpConfig)
          .success(succ)
          .error(err)
    }
    val httpCpy = HttpConfig.empty

    httpCpy.url = httpConfig.url
    httpCpy.method = httpConfig.method
    httpCpy.params = httpConfig.params
    httpCpy.headers = httpConfig.headers

    httpCpy

  }

}

