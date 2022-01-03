package com.example.udpclient.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
public class UdpController {
    @GetMapping("send")
    public String send(@RequestParam(value = "from", required = false, defaultValue = "9001") Integer from,
                       @RequestParam(value = "to", required = false, defaultValue = "9002") Integer to,
                       @RequestParam(value = "overtime", required = false, defaultValue = "5000") Long overtime) {
        // 准备好要发送的数据
        String content = "Hello World";
        // from -> to
        try (DatagramSocket ds = new DatagramSocket(from)) {
            // 实例化DatagramPacket对象，指定数据内容、数据长度、要发送的目标地址、发送端口
            DatagramPacket dp = new DatagramPacket(content.getBytes(), content.length(), InetAddress.getByName("localhost"), to);
            System.out.println("发送信息...");
            // 发送数据报
            ds.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        FutureTask<String> task = new FutureTask<>(() -> {
            // 循环监听 UDP 端口
            // for (int i = 0; i < 5; i++) {
            //     System.out.println(Thread.currentThread().getName());
            //     TimeUnit.SECONDS.sleep(1);
            // }

            // 定义接收数据的字节数组
            byte[] buf = new byte[1024];
            // 指定接收数据的端口为 to
            try (DatagramSocket ds = new DatagramSocket(from)) {
                // 指定接收数据的长度为1024
                DatagramPacket dp = new DatagramPacket(buf, 1024);
                System.out.println("等待接收数据...");
                // 接收数据
                ds.receive(dp);
                String res = new String(dp.getData(), 0, dp.getLength())
                        + "\nfrom " + dp.getAddress().getHostAddress()
                        + " : " + dp.getPort();
                // 处理返回结果
                // handle(res);
                System.out.println(res);
                return res;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "解析失败";
        });
        new Thread(task).start();
        try {
            return task.get(overtime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            if (TimeoutException.class == e.getClass()) {
                System.out.println("等待超时: " + overtime + " ms");
            }
        }
        task.cancel(true);
        return "发送失败";
    }
}
