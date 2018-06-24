package me.amanj.emr.log.viewer.io

import java.io.{File, PrintWriter}

import com.amazonaws.services.s3.AmazonS3ClientBuilder

import scala.collection.JavaConverters._

object S3Objects {
  val s3Client = AmazonS3ClientBuilder.defaultClient()

  def ls(bucket: String, prefix: String): Seq[String] = {
    s3Client.listObjects(bucket, prefix).getObjectSummaries.asScala.map(a => a.getKey)
  }

  def download(bucket: String, key: String, dest: String): Unit = {
    val objectStream = s3Client.getObject(bucket, key).getObjectContent
    val printer = new PrintWriter(new File(dest))
    scala.io.Source.fromInputStream(objectStream).foreach(ch =>
      printer.append(ch)
    )
    objectStream.close()
    printer.close()
  }
}
