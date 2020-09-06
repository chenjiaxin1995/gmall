package com.atguigu.gmall.realtime.app

import com.alibaba.fastjson.JSON
import com.atguigu.gmall.realtime.bean.StartupLog
import com.atguigu.gmall.realtime.util.{MyKafkaUtil, RedisUtil}
import com.atguigu.realtime.gmall.commom.Constant
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
 * @author devon
 * @date 2020-09-04-11:34
 */
object DauApp {

  val conf = new SparkConf().setMaster("local[2]").setAppName("DauApp")

  def main(args: Array[String]): Unit = {
    val ssc = new StreamingContext(conf,Seconds(3))

    val dstream
    = MyKafkaUtil.getKafkaStream(ssc,"DauApp",Set(Constant.STARTUP_TOPIC))
      .map(json=>JSON.parseObject(json,classOf[StartupLog]))

    val StartupStream = dstream.mapPartitions(x => {
      val client = RedisUtil.getClient

      val resoult = x.filter(y => {
        client.sadd(s"dau:uids:${y.logDate}", y.mid) == 1
      })

      client.close()
      resoult
    })

    StartupStream.foreachRDD(rdd => {
      import org.apache.phoenix.spark._
      rdd.saveToPhoenix("GMALL_DAU0421",
        Seq("MID", "UID", "APPID", "AREA", "OS", "CHANNEL", "LOGTYPE", "VERSION", "TS", "LOGDATE", "LOGHOUR"),
        zkUrl = Option("hadoop102,hadoop103,hadoop104:2181"))
    })

    ssc.start()
    ssc.awaitTermination()

  }
}
