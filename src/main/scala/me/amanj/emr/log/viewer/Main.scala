package me.amanj.emr.log.viewer

import java.io.File

import config.Config
import io.S3Objects
import io.Implicits._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global



object Main {

  val config = new Config("bucket", "clustreId", "destination", 100)


  def main(args: Array[String]): Unit = {

    val nodeFuture = Future[Unit] {
      S3Objects.ls(config.s3Bucket, config.clusterId / "node", config).foreach(downloadDirectory)
    }

    val stepsFuture = Future[Unit] {
      S3Objects.ls(config.s3Bucket, config.clusterId / "steps", config).foreach(downloadDirectory)
    }

    val containersFuture = Future[Unit]{
      S3Objects.ls(config.s3Bucket, config.clusterId / "containers", config).foreach(downloadDirectory)
    }

    // old school busy waiting, I believe this is safer than waiting for n time and hoping
    // that the computation is already done
    while(!containersFuture.isCompleted &&
      !stepsFuture.isCompleted &&
      !nodeFuture.isCompleted) {}

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
