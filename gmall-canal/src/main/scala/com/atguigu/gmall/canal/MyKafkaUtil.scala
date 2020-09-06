package com.atguigu.gmall.canal

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}



/**
 * @author devon
 * @date 2020-09-06-10:26
 */
object MyKafkaUtil {
  private val props = new Properties()
  props.put("bootstrap.servers", "hadoop102:9092,hadoop103:9092,hadoop104:9092")
  // key序列化
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  // value序列化
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  private val producer = new KafkaProducer[String,String](props)

  def send(topic : String,jsonString : String): Unit ={
    producer.send(new ProducerRecord[String,String](topic,jsonString))
  }
}
