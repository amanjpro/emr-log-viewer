package me.amanj.emr.log.viewer

import java.io.File

import config.{Config, FileConfig}
import io.S3Client
import io.Implicits._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Main {

  def main(args: Array[String]): Unit = {
    val config = FileConfig.loadConfig()
    val s3Client = new S3Client(config)
    downloadClusterLogs(s3Client, config)
  }

  def downloadClusterLogs(s3Client: S3Client, config: Config): Unit = {
    val nodeFuture = Future[Unit] {
      s3Client.ls(config.s3Bucket, config.s3ClusterId / "node", config).foreach(downloadDirectory(_, s3Client, config))
    }

    val stepsFuture = Future[Unit] {
      s3Client.ls(config.s3Bucket, config.s3ClusterId / "steps", config).foreach(downloadDirectory(_, s3Client, config))
    }

    val containersFuture = Future[Unit]{
      s3Client.ls(config.s3Bucket, config.s3ClusterId / "containers", config).foreach(downloadDirectory(_, s3Client, config))
    }

    // old school busy waiting, I believe this is safer than waiting for n time and hoping
    // that the computation is already done
    while(!containersFuture.isCompleted &&
      !stepsFuture.isCompleted &&
      !nodeFuture.isCompleted) {}
  }

  def downloadDirectory(key: String, s3Client: S3Client, config: Config): Unit = {

    val relativePath =
      key
        .replace(config.s3Bucket / config.s3ClusterId, "")
        .platformIndependentPath

    val directoryPath = relativePath.parentDirecotry

    new File(config.destination / config.s3ClusterId / directoryPath).mkdirs()

    s3Client.download(config.s3Bucket, config.s3ClusterId, config.destination / config.s3ClusterId / relativePath)
  }
}
