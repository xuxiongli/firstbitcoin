package com.xuxiongli.fristbitcoin;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Scanner;

@SpringBootApplication
public class FristbitcoinApplication {

    //自己定义的端口号port
    public static String port;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        port = scanner.nextLine();

        new SpringApplicationBuilder(FristbitcoinApplication.class).properties("server.port=" + port).run(args);
    }
}
