package me.amanj.emr.log.viewer.config

import java.util.Properties
import java.io.FileInputStream
import java.util.logging.Level

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
        // strictly required
        accessKey = properties.getOr[Nothing](Constants.accessKeyPropertiesKey) {
          logger.log(Level.SEVERE, unsetPropertyMessage(Constants.accessKeyPropertiesKey))
          throw new BadConfigException(unsetPropertyMessage(Constants.accessKeyPropertiesKey))
        },
        secretKey = properties.getOr[Nothing](Constants.secretKeyPropertiesKey) {
          logger.log(Level.SEVERE, unsetPropertyMessage(Constants.secretKeyPropertiesKey))
          throw new BadConfigException(unsetPropertyMessage(Constants.secretKeyPropertiesKey))
        },
        s3Bucket = properties.getOr[Nothing](Constants.bucketPropertiesKey) {
          logger.log(Level.SEVERE, unsetPropertyMessage(Constants.bucketPropertiesKey))
          throw new BadConfigException(unsetPropertyMessage(Constants.bucketPropertiesKey))
        },

        // defaults available
        fileDownloadPagination = properties.getWithDefault(Constants.paginationPropertiesKey)
          (v => Try(v.toInt).toOption)
          {
            val key = Constants.paginationPropertiesKey
            logger.warning(
              s"""|Warning $key should be set to an integer value,
                  |${properties.getProperty(key)} is not an integer value.
                  |Using default value ${Constants.DefaultDownloadPagination}""".stripMargin)
            Constants.DefaultDownloadPagination
          },
        destination = properties.getOr[String](Constants.destinationPropertiesKey)(
          Constants.DefaultDownloadDestination),

        // can be null or empty strings
        s3ClusterId = properties.getProperty(Constants.clusterIdPropertiesKey),
      )
    }
    Instance.get
  }

  implicit class PropertiesOps(val properties: Properties) extends AnyVal {
    def getOr[R <: String](key: String)(orElse: =>R): String = {
      getOr[String, R](key)(identity)(orElse)
    }

    def getWithDefault[T, R <: T](key: String)(fun: String => Option[T])(orElse: =>R): T = {
      val value = properties.getProperty(key)
      if(value == null && value.isEmpty) orElse
      else fun(value).getOrElse(orElse)
    }

    def getOr[T, R <: T](key: String)(fun: String => T)(orElse: =>R): T = {
      val value = properties.getProperty(key)
      if(value == null && value.isEmpty) orElse
      else fun(value)
    }
  }

  def unsetPropertyMessage(property: String): String = s"$property is not set"
}
