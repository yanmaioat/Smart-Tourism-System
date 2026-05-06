package com.example.controller;

import com.example.service.BaiduMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BaiduMapController {

    @Autowired
    private BaiduMapService baiduMapService;

    @GetMapping("/getRoute")
    public String getRoute(@RequestParam double originLat,
                           @RequestParam double originLng,
                           @RequestParam double destLat,
                           @RequestParam double destLng) {
        return baiduMapService.getRoute(originLat, originLng, destLat, destLng);
    }
}
