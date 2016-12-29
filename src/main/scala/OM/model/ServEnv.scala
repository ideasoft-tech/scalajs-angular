

package OM.model

import scala.scalajs.js
import scala.scalajs.js.JSConverters.{JSRichGenMap, genTravConvertible2JSRichGenTrav}
import scala.scalajs.js.annotation.{JSExport, JSExportAll}

@JSExportAll
object ApiClient {

  type Environments = List[EnvName]

  type EnvName = String

  type GroupName = String

  type VarName = String

  type ConfigMetadata = Map[GroupName, Environments]

  type CurrentConfig = Map[GroupName, EnvName]

  type Variables = Map[(GroupName, VarName, EnvName), String]

  @JSExportAll
  trait ServiceEnvironment {

    def metadata: ConfigMetadata

    def current: CurrentConfig

    def variables: Variables


    def setCurrent(group: String, env: String): ServiceEnvironment

    def getVariableValue(group: GroupName, varName: VarName): String = variables((group, varName, current(group)))

  }

  @JSExportAll
  case class BasicEnvironment(metadata: ConfigMetadata,
                              current: CurrentConfig,
                              variables: Variables) extends ServiceEnvironment {
    def setCurrent(group: String, env: String): BasicEnvironment = this.copy(current = current + (group -> env))

    @JSExport
    def getGroups: js.Array[String] = metadata.keySet.toJSArray

    @JSExport
    def getVariables(group: String, env: String) = variables.keys
      .filter(k => k._1 == group && k._3 == env)
      .map(k => k._2 -> variables((group, k._2, env))).toMap.toJSDictionary


    @JSExport
    def getEnvs(env: String): js.Array[String] = metadata.getOrElse(env, List()).toJSArray

  }

  @JSExportAll
  object BasicEnvironment {
    def apply(metadata: ConfigMetadata, variables: Variables): BasicEnvironment = {
      val current = metadata.map { case (g, l) => (g, l.head) }
      BasicEnvironment(metadata, current, variables)
    }
  }


}