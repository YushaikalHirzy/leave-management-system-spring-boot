package com.example.leavemanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LeaveManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeaveManagementSystemApplication.class, args);
    }

}
