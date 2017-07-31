package de.dkt.eservices.enlg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableAutoConfiguration(exclude = {
	    org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
	    org.springframework.boot.actuate.autoconfigure.ManagementSecurityAutoConfiguration.class})
public class ServerExecution {

	public static void main(String[] args) {
//		SpringApplication.run(ENLGRestController.class, args);
		SpringApplication.run(ServerExecution.class, args);
	}
	
}
