package me.amanj.emr.log.viewer

import java.io.File
import io.S3Objects

object Main {

  def main(args: Array[String]): Unit = {

    val clusterId: String = ???
    val s3Bucket: String = ???
    val dest: String = ???
    val / = System.lineSeparator()

    S3Objects.ls(s3Bucket, clusterId).foreach { key =>
      val relativePath =
        key
          .replaceAll(s"^$s3Bucket/$clusterId/", "")
          .replaceAll("/", if (/ == "\\") "\\\\" else /)

      val directoryPath = relativePath.substring(0, relativePath.lastIndexOf(/))
      new File(s"$dest$/$clusterId$directoryPath").mkdirs()
      S3Objects.download(s3Bucket, clusterId, s"$dest$/$clusterId$relativePath")
    }
  }
}
