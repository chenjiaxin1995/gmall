package com.atguigu.realtime.gmallpublisher.service;

import java.util.Map;

public interface PublisherService {
    Long getDau(String date);

    Map<String, Long> getHourDau(String date);
}
