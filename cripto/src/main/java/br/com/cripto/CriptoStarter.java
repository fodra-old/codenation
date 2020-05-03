package br.com.cripto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import br.com.cripto.clients.CodenationClient;

@SpringBootApplication
public class CriptoStarter {

	public static void main(String[] args) {
		
		ConfigurableApplicationContext context = SpringApplication.run(CriptoStarter.class, args);
		
		CodenationClient cc = context.getBean(CodenationClient.class);
		cc.get();
//		cc.post();
	}
}
