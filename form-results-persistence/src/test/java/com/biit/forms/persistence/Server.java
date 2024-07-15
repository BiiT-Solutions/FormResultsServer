package com.biit.forms.persistence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

@SpringBootApplication
@Service
public class Server {
    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }
}
