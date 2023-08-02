package com.example.kafka;

import org.apache.kafka.clients.admin.NewTopic;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@SpringBootApplication
public class KafkaApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaApplication.class, args);
	}

	@KafkaListener(id = "crac", topics = "crac")
	public void listen(Greeting in) {
		System.out.println("++++++Received: " + in);
	}

	@Bean
	public NewTopic topic() {
		return TopicBuilder.name("crac").partitions(1).replicas(1).build();
	}

	@Bean
	public ApplicationRunner runner(KafkaTemplate<Object, Object> template, ConsumerFactory<Object, Object> cf) {

		cf.addListener(new ConsumerFactory.Listener<>() {
		});
		return args -> {
			Greeting data = new Greeting("Hello from Coordinated Restore at Checkpoint!");
			template.send("crac", data);
			System.out.println("++++++Sent: " + data);
		};
	}

	public static class Greeting {

		private final String message;

		@JsonCreator
		public Greeting(@JsonProperty("message") String message) {
			this.message = message;
		}

		public String getMessage() {
			return this.message;
		}

		@Override
		public String toString() {
			return "Greeting{" + "message='" + this.message + '\'' + '}';
		}

	}

}