package me.amanj.emr.log.viewer

import java.io.File
import config.Config
import io.S3Objects

import io.Implicits._

object Main {

  val config = new Config("bucket", "clustreId", "destination", 100)

  def main(args: Array[String]): Unit = {
    S3Objects.ls(config.s3Bucket, config.clusterId / "node", config).foreach(downloadDirectory)

    S3Objects.ls(config.s3Bucket, config.clusterId / "steps", config).foreach(downloadDirectory)

    S3Objects.ls(config.s3Bucket, config.clusterId / "containers", config).foreach(downloadDirectory)
  }

  def downloadDirectory(key: String): Unit = {

    val relativePath =
      key
        .replace(config.s3Bucket / config.clusterId, "")
        .platformIndependentPath

    val directoryPath = relativePath.parentDirecotry

    new File(config.destination / config.clusterId / directoryPath).mkdirs()

    S3Objects.download(config.s3Bucket, config.clusterId, config.destination / config.clusterId / relativePath)
  }
}
