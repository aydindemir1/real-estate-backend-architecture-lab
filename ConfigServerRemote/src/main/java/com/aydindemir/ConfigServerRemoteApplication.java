package com.aydindemir;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class ConfigServerRemoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerRemoteApplication.class, args);
    }
}
