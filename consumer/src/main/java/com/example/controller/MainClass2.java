package com.example.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class MainClass2 {
    @Autowired
    DiscoveryClient discoveryClient;

    @Qualifier("eurekaClient")
    @Autowired
    EurekaClient eurekaClient;

    // 负载均衡，类似于使用ribbon
    @Autowired
    LoadBalancerClient lb;

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/client5")
    public String client5(){
        List<InstanceInfo> instances = eurekaClient.getInstancesByVipAddress("provider", false);
        boolean isUp = false;
        ServiceInstance si = lb.choose("provider");
        for (InstanceInfo ins : instances) {
            if (ins.getHostName().equalsIgnoreCase(ins.getHostName()) && ins.getStatus() == InstanceInfo.InstanceStatus.UP) {
                isUp = true;
                break;
            }
        }
        if (!isUp) {
            return "当前没有up状态的服务";
        }

        String url="http://" + si.getHost() + ":" + si.getPort() + "/getHi";
        String repstr = restTemplate.getForObject(url, String.class);
        return repstr;
    }


}
