package com.example.udpserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.ip.udp.UnicastReceivingChannelAdapter;
import org.springframework.messaging.Message;

@Configuration
public class UdpReceiveConfiguration {
    @Bean
    public UnicastReceivingChannelAdapter getUnicastReceivingChannelAdapter() {
        // 实例化一个 UDP 9003 端口
        UnicastReceivingChannelAdapter adapter = new UnicastReceivingChannelAdapter(9003);
        adapter.setOutputChannelName("udp");
        return adapter;
    }

    @Transformer(inputChannel = "udp", outputChannel = "handle")
    public String transformer(Message<?> message) {
        // 把接收的数据转化为字符串
        return new String((byte[]) message.getPayload());
    }

    @ServiceActivator(inputChannel = "handle")
    public void udpMessageHandle(String message) {
        System.out.println("udp1: " + message);
    }
}
