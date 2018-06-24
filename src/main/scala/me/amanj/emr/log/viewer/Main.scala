package me.amanj.emr.log.viewer

import java.io.File
import io.S3Objects
import io.Implicits._

object Main {

  val clusterId: String = ???
  val s3Bucket: String = ???
  val dest: String = ???

  def main(args: Array[String]): Unit = {
    S3Objects.ls(s3Bucket, clusterId / "node").foreach(downloadDirectory)

    S3Objects.ls(s3Bucket, clusterId / "steps").foreach(downloadDirectory)

    S3Objects.ls(s3Bucket, clusterId / "containers").foreach(downloadDirectory)
  }

  def downloadDirectory(key: String): Unit = {

    val relativePath =
      key
        .replace(s3Bucket / clusterId, "")
        .platformIndependentPath

    val directoryPath = relativePath.parentDirecotry

    new File(dest / clusterId / directoryPath).mkdirs()

    S3Objects.download(s3Bucket, clusterId, dest / clusterId / relativePath)
  }
}
