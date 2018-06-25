package me.amanj.emr.log.viewer

import java.util.logging.{Logger => JLogger}

object Logger {
  @transient val logger = JLogger.getLogger(Main.getClass.getName)
}
