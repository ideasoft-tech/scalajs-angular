package OM.model

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

/**
  * Created by jvelazquez on 12/27/2016.
  */

@JSExportAll
trait TreeMenu {
  def isLeaf: Boolean
}

@JSExportAll
class MenuFolder(var label: String, var children: js.Array[TreeMenu] = js.Array()) extends TreeMenu {
  override def isLeaf: Boolean = false
}

@JSExportAll
class MenuItem(var label: String, description: String, var serviceID: String) extends TreeMenu {
  override def isLeaf: Boolean = true
}


object TreeMenuBuilder {

  def build(nodes: js.Array[OMModel]): TreeMenu = {

    val tree = new MenuFolder("root", js.Array())

    nodes.foreach(n => aux(tree, n.definitionPath.split('/').toList, n))

    tree
  }

  def aux(treeMenu: MenuFolder, path: List[String], oMModel: OMModel): Unit = {
    if (path.isEmpty) {

      treeMenu.children.push(new MenuItem(oMModel.name, oMModel.description, oMModel.getID))

    } else {

      val oldParent = treeMenu.children.find(p => !p.isLeaf && p.asInstanceOf[MenuFolder].label == path.head).orNull
      if (oldParent == null) {

        val newTree = new MenuFolder(path.head, js.Array())

        treeMenu.children.push(newTree)
        aux(newTree, path.tail, oMModel)

      } else {
        val oldMenu = oldParent.asInstanceOf[MenuFolder]

        aux(oldMenu, path.tail, oMModel)
      }

    }
  }
}
