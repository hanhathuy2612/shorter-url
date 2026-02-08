package com.huyhn.shorter_url_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShorterUrlBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShorterUrlBackendApplication.class, args);
    }

}
