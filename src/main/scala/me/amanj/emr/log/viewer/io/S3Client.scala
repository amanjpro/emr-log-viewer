package me.amanj.emr.log.viewer.io

import java.io.{File, PrintWriter}

import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider}
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.{ListObjectsV2Request, S3ObjectSummary}
import me.amanj.emr.log.viewer.config.Config

import scala.annotation.tailrec
import scala.collection.JavaConverters._

class S3Client(config: Config) {
  @transient val s3Client = AmazonS3ClientBuilder.standard()
      .withCredentials(new AWSCredentialsProvider {
        override def getCredentials(): AWSCredentials = {
          new AWSCredentials {
            override def getAWSAccessKeyId: String = config.accessKey

            override def getAWSSecretKey: String = config.secretKey
          }
        }
        override def refresh(): Unit = { }
      })
      .withRegion(config.s3Region)
      .build();

  @tailrec
  private[this] def ls(request: ListObjectsV2Request, acc: Iterator[S3ObjectSummary]): Iterator[S3ObjectSummary] = {
    val result = s3Client.listObjectsV2(request)
    val objects: Iterator[S3ObjectSummary] = acc ++ result.getObjectSummaries.asScala.toVector
    if(result.isTruncated) {
      request.setContinuationToken(result.getContinuationToken)
      ls(request, objects)
    } else {
      objects
    }
  }

  private[this] def allKeys(bucket: String, prefix: String,
                            acc: Iterator[String], paginationSize: Int): Iterator[String] = {
    val request = new ListObjectsV2Request()
      .withBucketName(bucket)
      .withPrefix(prefix)
      .withMaxKeys(paginationSize)

    val objects = ls(request, Iterator.empty)

    objects.flatMap { obj =>
      if(s3Client.doesObjectExist(bucket, obj.getKey)) Iterator(obj.getKey)
      else allKeys(bucket, obj.getKey, Iterator.empty, paginationSize)
    }
  }

  def ls(bucket: String, prefix: String, config: Config): Iterator[String] = {
    allKeys(bucket, prefix, Iterator.empty, config.fileDownloadPagination)
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
