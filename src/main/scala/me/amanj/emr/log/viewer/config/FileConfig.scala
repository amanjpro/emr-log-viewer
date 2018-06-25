package me.amanj.emr.log.viewer.config

import java.util.Properties
import java.io.FileInputStream

import me.amanj.emr.log.viewer.io.Implicits._
import me.amanj.emr.log.viewer.Logger.logger

import scala.util.Try

object FileConfig {
  private[this] var Instance: Option[Config] = None

  def loadConfig(): Config = {
    if(Instance.isDefined) Instance.get
    else reloadConfig()
  }

  def reloadConfig(): Config = {
    val properties = new Properties()
    val input = new FileInputStream(
      System.getProperty("user.home") / s".${Constants.appName}" / Constants.propertiesFile)
    properties.load(input)

    Instance = Some {
      Config(
        accessKey = properties.getProperty(Constants.accessKeyPropertiesKey),
        secretKey = properties.getProperty(Constants.secretKeyPropertiesKey),
        s3Bucket = properties.getProperty(Constants.bucketPropertiesKey),
        s3ClusterId = properties.getProperty(Constants.clusterIdPropertiesKey),
        fileDownloadPagination = Try {
          properties.getProperty(Constants.paginationPropertiesKey).toInt
        }.getOrElse {
          val key = Constants.paginationPropertiesKey
          logger.warning(
            s"Warning $key should be set to an integer value, ${properties.getProperty(key)} is not an integer value")
          Constants.DefaultDownloadPagination
        },
        destination = properties.getProperty(Constants.destinationPropertiesKey),
      )
    }
    Instance.get
  }
}
