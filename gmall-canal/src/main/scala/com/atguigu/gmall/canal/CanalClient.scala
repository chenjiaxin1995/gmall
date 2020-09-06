package com.atguigu.gmall.canal

import java.net.{InetSocketAddress, SocketAddress}
import java.util

import com.alibaba.fastjson.JSONObject
import com.alibaba.otter.canal.client.CanalConnectors
import com.alibaba.otter.canal.protocol.CanalEntry
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange

import scala.collection.JavaConverters._
/**
 * @author devon
 * @date 2020-09-05-16:25
 */
object CanalClient {

  private var obj = new JSONObject()

  def handleRowData(rowDatasList: util.List[CanalEntry.RowData],
                    tableName: String,
                    eventType: CanalEntry.EventType) = {
    if(!rowDatasList.isEmpty && tableName.equals("order_info") && eventType.equals(CanalEntry.EventType.INSERT)){
      for (rowData <- rowDatasList.asScala) {
        println(rowData)
        val columns = rowData.getAfterColumnsList.asScala
        obj.clear()
        for (column <- columns) {
          obj.put(column.getName,column.getValue)
        }
          MyKafkaUtil.send(tableName,obj.toString)
      }
    }
  }

  def main(args: Array[String]): Unit = {
    //连接canal，并指定需要连接的实例
    val add: SocketAddress = new InetSocketAddress("hadoop102",11111)
    val conn = CanalConnectors.newSingleConnector(add,"example","","")

    //连接
    conn.connect()

    //订阅example中的某个库/表
    conn.subscribe("gmall0421.*")

    //拉取数据
    while(true){
      //拉取100条sql语句导致的数据变化
      val message = conn.get(100)
      val entries = message.getEntries
      if(entries != null && !entries.isEmpty){
        for (elem <- entries.asScala) {
          if(elem != null && elem.hasEntryType && elem.getEntryType.equals(CanalEntry.EntryType.ROWDATA)){

            val storeValue = elem.getStoreValue
            val rowChange = RowChange.parseFrom(storeValue)
            val rowDatasList = rowChange.getRowDatasList
            handleRowData(rowDatasList,elem.getHeader.getTableName,rowChange.getEventType)
          }
        }
      }else{
        Thread.sleep(3000)
      }
    }
  }
}
