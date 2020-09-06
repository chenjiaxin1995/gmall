package com.atguigu.gmall.realtime.util

import java.util.Properties

/**
 * @author devon
 * @date 2020-09-04-11:20
 */
object ConfigUtil {

  val is = ClassLoader.getSystemResourceAsStream("config.properties")
  val properties = new Properties()
  properties.load(is)

  def getConf(name: String) = {
    properties.getProperty(name)
  }


}
