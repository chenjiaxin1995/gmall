package com.atguigu.realtime.gmalllog;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.realtime.gmall.commom.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author lzc
 * @Date 2020/9/2 11:30
 */
/*@Controller
@ResponseBody*/
@RestController  // == @Controller + @ResponseBody
public class PublisherController {
    @PostMapping("/log")
    public String doLog(String log) {
        // 1. 添加时间戳
        log = addTs(log);
        System.out.println(log);
        // 2. 落盘(给离线需求使用)
        saveLog(log);
        // 3. 把数据发动到kafka
        sendToKafka(log);


        return "ok";
    }

    @Autowired
    KafkaTemplate<String, String> kafka;

    private void sendToKafka(String log) {
        if (log.contains("startup")) {
            kafka.send(Constant.STARTUP_TOPIC, log);
        } else {
            kafka.send(Constant.EVENT_TOPIC, log);
        }
    }

    private void saveLog(String log) {
        Logger logger = LoggerFactory.getLogger(PublisherController.class);
        logger.info(log);
    }

    /**
     * 给日志添时间戳
     *
     * @param log
     */
    private String addTs(String log) {
        JSONObject js = JSON.parseObject(log);
        js.put("ts",System.currentTimeMillis());
        return js.toJSONString();
    }
}
