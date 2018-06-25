package me.amanj.emr.log.viewer.config

object Constants {
  @transient val DefaultDownloadPagination = 100

  // App properties
  val appName = "elv"
  val propertiesFile = "elv.properties"

  // properties keys
  val accessKeyPropertiesKey = "elv.s3.access.key"
  val secretKeyPropertiesKey = "elv.s3.secret.key"
  val bucketPropertiesKey = "elv.s3.emr.bucket"
  val clusterIdPropertiesKey = "elv.s3.emr.cluster.id"
  val destinationPropertiesKey = "elv.s3.emr.destination"
  val paginationPropertiesKey = "elv.file.download.pagination"
}
