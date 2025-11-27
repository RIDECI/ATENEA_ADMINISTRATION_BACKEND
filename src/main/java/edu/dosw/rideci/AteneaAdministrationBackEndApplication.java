package edu.dosw.rideci;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class AteneaAdministrationBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(AteneaAdministrationBackEndApplication.class, args);
	}

}
