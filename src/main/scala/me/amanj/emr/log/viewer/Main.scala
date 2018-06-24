package me.amanj.emr.log.viewer

import java.io.File
import io.S3Objects
import io.Implicits._

object Main {

  def main(args: Array[String]): Unit = {

    val clusterId: String = ???
    val s3Bucket: String = ???
    val dest: String = ???

    S3Objects.ls(s3Bucket, clusterId).foreach { key =>
      val relativePath =
        key
          .replace(s3Bucket / clusterId, "")
          .platformIndependentPath

      val directoryPath = relativePath.parentDirecotry

      new File(dest / clusterId / directoryPath).mkdirs()

      S3Objects.download(s3Bucket, clusterId, dest / clusterId / relativePath)
    }
  }
}
