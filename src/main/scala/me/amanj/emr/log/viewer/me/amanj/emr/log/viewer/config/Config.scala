package me.amanj.emr.log.viewer.config

case class Config(s3Bucket: String,
                  clusterId: String,
                  destination: String,
                  paginationSize: Int)
