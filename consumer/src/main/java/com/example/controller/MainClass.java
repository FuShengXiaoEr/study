package com.example.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class MainClass {
    @Autowired
    DiscoveryClient discoveryClient;

    @Qualifier("eurekaClient")
    @Autowired
    EurekaClient eurekaClient;

    // 负载均衡，类似于使用ribbon
    @Autowired
    LoadBalancerClient lb;

    @GetMapping("/getHi")
    public String getHi(){
        return "hi";
    }

    @GetMapping("/client")
    public String client(){
        List<String> clients = discoveryClient.getServices();
//        System.out.println(ToStringBuilder.reflectionToString(clients));
        clients.forEach(p -> {
            System.out.println(p);
        });
        return "Hi";
    }

    @GetMapping("/client2")
    public Object client2(){
        return discoveryClient.getInstances("provider");
    }

    @GetMapping("/client3")
    public Object client3(){
        List<InstanceInfo> infoSingle = eurekaClient.getInstancesById("DESKTOP-IAJP880:consumer:90");
        System.out.println(infoSingle);
        List<InstanceInfo> infos = eurekaClient.getInstancesByVipAddress("provider",false);
        if (infos.size() > 0) {
            // 服务
            InstanceInfo instanceInfo = infos.get(0);
            if (instanceInfo.getStatus() == InstanceInfo.InstanceStatus.UP) {
                String url = "http://" + instanceInfo.getHostName()  + ":" + instanceInfo.getPort() + "/getHi";
                System.out.println("url: " + url);
                RestTemplate rest = new RestTemplate();
                String repStr = rest.getForObject(url,String.class);
                System.out.println("repStr:" + repStr);
            }
        }
        return "client4";
    }

    @GetMapping("/client4")
    public Object client4(){
        // ribbon完成客户端的负载均衡，过滤掉了down了的节点
        ServiceInstance instance = lb.choose("provider");
        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/getHi";
        System.out.println("url: " + url);
        RestTemplate rest = new RestTemplate();
        String repStr = rest.getForObject(url,String.class);
        System.out.println("repStr:" + repStr);
        return "client4";
    }

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
