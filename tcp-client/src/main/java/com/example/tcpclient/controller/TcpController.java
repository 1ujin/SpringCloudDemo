package com.example.tcpclient.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

@RestController
public class TcpController {

    @GetMapping("send")
    public String send(@RequestParam(value = "from", required = false, defaultValue = "9005") Integer from,
                       @RequestParam(value = "to", required = false, defaultValue = "9004") Integer to,
                       @RequestParam(value = "overtime", required = false, defaultValue = "5000") Long overtime) {
        ExecutorService exec = Executors.newCachedThreadPool();
        try (
                Socket socket = new Socket("localhost", to, null, from);
                OutputStream os = socket.getOutputStream()) {
            socket.setReuseAddress(true);
            // 准备好要发送的数据
            String req = "Hello World";
            System.out.println("发送请求...");
            os.write(req.getBytes(StandardCharsets.UTF_8));
            FutureTask<String> task = new FutureTask<>(() -> {
                // from -> to
                try (InputStream is = socket.getInputStream()) {
                    byte[] buff = new byte[0x400];
                    int len = is.read(buff);
                    String s = new String(buff, 0, len, StandardCharsets.UTF_8);
                    String ip = socket.getInetAddress().getHostAddress();
                    int port = socket.getPort();
                    String res = s + "\nfrom " + ip + ":" + port;
                    System.out.println(res);
                    return res;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "解析失败";
            });
            System.out.println("等待接收响应...");
            exec.execute(task);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "发送失败";
    }

}
