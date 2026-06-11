package com.keyur.queue_x;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class QueueXApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueueXApplication.class, args);
	}

}
