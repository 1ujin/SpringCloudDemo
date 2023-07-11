package com.example.tcpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class TcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TcpServerApplication.class, args);
        ExecutorService exec = Executors.newFixedThreadPool(3);
        AtomicInteger count = new AtomicInteger(0);
        try (ServerSocket server = new ServerSocket(9004, 3)) {
            server.setReuseAddress(true);
            while (true) {
                System.out.println("线程数量: " + ((ThreadPoolExecutor) exec).getActiveCount());
                System.out.println("等待建立连接...");
                Socket socket = server.accept();
                System.out.println("已建立连接数量: " + count.incrementAndGet());
                exec.execute(() -> {
                    try (
                            InputStream is = socket.getInputStream();
                            OutputStream os = socket.getOutputStream()) {
                        String ip = socket.getInetAddress().getHostAddress();
                        int port = socket.getPort();
                        System.out.println("等待接收数据...");
                        byte[] buff = new byte[0x400];
                        int len = is.read(buff);
                        String s = new String(buff, 0, len, StandardCharsets.UTF_8);
                        // 如果用 readAllBytes 接收数据会阻塞直到输出流关闭
                        // String s = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                        System.out.println(s + "\nfrom " + ip + ":" + port);
                        os.write(s.getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        count.decrementAndGet();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
