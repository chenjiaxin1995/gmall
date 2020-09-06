package com.atguigu.gmall.realtime.bean

import java.text.SimpleDateFormat
import java.util.Date

/**
 * @author devon
 * @date 2020-09-04-10:58
 */
case class StartupLog(mid: String,
                      uid: String,
                      appId: String,
                      area: String,
                      os: String,
                      channel: String,
                      logType: String,
                      version: String,
                      ts: Long,
                      var logDate: String = null,
                      var logHour: String = null){
  private val date = new Date(ts)
  logDate= new SimpleDateFormat("yyyy-MM-dd").format(date)
  logHour = new SimpleDateFormat("HH").format(date)
}

