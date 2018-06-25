package me.amanj.emr.log.viewer.config

case class Config(accessKey: String,
                  secretKey: String,
                  s3Bucket: String,
                  s3ClusterId: String,
                  fileDownloadPagination: Int,
                  destination: String,
                 )

