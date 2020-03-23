package br.kuhnen.menssages;

import br.kuhnen.menssages.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class MenssagesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MenssagesApplication.class, args);
	}

}
