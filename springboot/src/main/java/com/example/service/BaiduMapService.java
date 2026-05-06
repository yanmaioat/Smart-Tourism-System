package com.example.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BaiduMapService {

    private static final String BAIDU_MAP_API_URL = "https://api.map.baidu.com/direction/v2/driving";
    private static final String AK = "YOUR_BAIDU_API_KEY";  // 替换为你的API Key

    private RestTemplate restTemplate = new RestTemplate();

    public String getRoute(double originLat, double originLng, double destLat, double destLng) {
        // 拼接请求 URL
        String url = String.format("%s?origin=%f,%f&destination=%f,%f&ak=%s",
                BAIDU_MAP_API_URL, originLat, originLng, destLat, destLng, AK);

        // 使用 RestTemplate 调用百度地图 API
        return restTemplate.getForObject(url, String.class);
    }
}
