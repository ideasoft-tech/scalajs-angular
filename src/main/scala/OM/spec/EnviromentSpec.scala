package OM.spec

import OM.model.ApiClient.{BasicEnvironment, ConfigMetadata}
import OM.model._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

/**
  * Created by jvelazquez on 12/26/2016.
  */

@JSExportAll
object EnviromentSpec {

  val metadata: ConfigMetadata = Map(
    "OM" -> List("localhost", "PRO-node1", "om-client"),
    "Users" -> List("IS User", "BO Centro1", "BO Montevideo1"))

  val variables = Map(
    ("OM", "host", "localhost") -> "http://localhost:8989",

    ("OM", "host", "PRO-node1") -> "https://10.34.140.29:443",

    ("OM", "host", "om-client") -> "http://om-client:8989/om/v1/admin",
    ("OM", "user", "om-client") -> "ecommerce",
    ("OM", "password", "om-client") -> "ecommerce"
  )

  val context = BasicEnvironment(metadata, variables)

  val spec = js.Array[OMModel](

    new OMModel(
      name = "Alta versión a Catalogo por Servicio",
      url = "dataResources/{resourceName}/versions",
      method = "POST",
      soapName = "getDataResourceById",
      definitionPath = "OMAdmin/Catálogos/",
      description = "Obtiene el xml que representa el data resource especificado en el parametro resourceName",
      requiredFields = js.Array(

        new Panel(
          label = "Campos Requeridos",
          content = js.Array(
            new Textfield("Nombre del recurso", "resourceName", RequestBind.PARAMETER)
          )
        ),

        new Panel(
          label = "Datos del recurso",
          content = js.Array(
            new PayloadXML(
              "",
              "",
              RequestBind.BODY,
              "<dataResourceVersion xmlns=\"http://ns.antel.com.uy/schema/api/resources-v1\">",
              "</dataResourceVersion>",
              js.Array(
                new Textfield("Nombre", "name", RequestBind.PARAMETER),
                new Textfield("Válido desde", "validFrom", RequestBind.PARAMETER),
                new Textfield("Sub path", "subPath", RequestBind.PARAMETER),
                new Combobox("Estado", "state", RequestBind.PARAMETER, js.Array(js.Tuple2("INACTIVE", "inactive"), js.Tuple2("ACTIVE", "active"), js.Tuple2("DRAFT", "draft")))
              )
            )
          )
        ),

        new HeaderPanel(js.Dictionary("Content-Type" -> "application/xml"))
      )
    ),
    new OMModel(
      name = "Alta Catalogo por Archivo",
      url = "dataResources/{resourceName}/versions/{versionId}/payload",
      method = "POST",
      soapName = "addDataResourcePayLoad",
      definitionPath = "OMAdmin/Catálogos/",
      description = "Agrega un payload a la version especificada en el parámetro versionId del padre especificado en el parámetro resourceName. ",
      requiredFields = js.Array(
        new Panel(
          "Compos requeridos",
          js.Array(
            new Textfield("Nombre de recurso", "resourceName", RequestBind.PARAMETER),
            new Textfield("Versión ID", "versionId", RequestBind.PARAMETER),
            new File(
              label = "",
              key = "",
              RequestBind.BODY
            )
          )
        )
      )
    )
  )
}
