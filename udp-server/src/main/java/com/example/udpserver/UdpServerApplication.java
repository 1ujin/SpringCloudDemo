package com.example.udpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

@SpringBootApplication
public class UdpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UdpServerApplication.class, args);
        // 定义接收数据的字节数组
        byte[] buf = new byte[1024];
        // 指定接收数据的端口为9000
        try (DatagramSocket ds = new DatagramSocket(9002)) {
            // 指定接收数据的长度为1024
            DatagramPacket dp1 = new DatagramPacket(buf, 1024);
            System.out.println("等待接收数据...");
            while (true) {
                // 接收数据
                ds.receive(dp1);
                String receive = new String(dp1.getData(), 0, dp1.getLength())
                        + "\nfrom " + dp1.getAddress().getHostAddress()
                        + " : " + dp1.getPort();
                // 处理返回结果
                // handle(receive);
                // 实例化DatagramPacket对象，指定数据内容、数据长度、要发送的目标地址、发送端口
                DatagramPacket dp2 = new DatagramPacket(receive.getBytes(), receive.length(), InetAddress.getByName("localhost"), 9001);
                System.out.println(receive);
                System.out.println("发送信息...");
                // 发送数据报
                ds.send(dp2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
