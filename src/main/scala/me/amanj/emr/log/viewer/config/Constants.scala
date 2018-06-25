package me.amanj.emr.log.viewer.config

import me.amanj.emr.log.viewer.io.Implicits._

object Constants {
  @transient val DefaultDownloadPagination = 100
  @transient val DefaultDownloadDestination = "user.home" / s".${Constants.appName}" / "cache"

  // App properties
  val appName = "elv"
  val propertiesFile = "elv.properties"

  // properties keys
  val accessKeyPropertiesKey = "elv.s3.access.key"
  val secretKeyPropertiesKey = "elv.s3.secret.key"
  val bucketPropertiesKey = "elv.s3.emr.bucket"
  val regionPropertiesKey = "elv.s3.emr.region"
  val clusterIdPropertiesKey = "elv.s3.emr.cluster.id"
  val destinationPropertiesKey = "elv.s3.emr.destination"
  val paginationPropertiesKey = "elv.file.download.pagination"
}
