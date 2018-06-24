package me.amanj.emr.log.viewer.io

object Implicits {
  implicit class PathOps(val path: String) extends AnyVal {
    def /(child: String): String = s"$path${System.lineSeparator()}$child"
    def parentDirecotry: String = {
      val sep = System.lineSeparator()
      val endPos = path.lastIndexOf(sep)
      path.substring(0, endPos)
    }

    def platformIndependentPath: String = path.replace("/", System.lineSeparator())
  }
}
