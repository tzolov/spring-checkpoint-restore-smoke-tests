/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.amqp;

import java.util.concurrent.TimeUnit;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.SendTo;

@SpringBootApplication
public class AmqpRabbitApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(AmqpRabbitApplication.class, args).close();
	}

	@Autowired
	RabbitTemplate template;

	@Autowired
	RabbitTemplate confirmTemplate;

	@RabbitListener(id = "cr1", queues = "cr1")
	@SendTo
	public String upperCaseIt(String in) {
		try {
			String two = sendWithConfirms();
			return in.toUpperCase() + two;
		}
		catch (Exception e) {
			throw new AmqpRejectAndDontRequeueException("fail");
		}
	}

	@RabbitListener(id = "cr2", queues = "cr2")
	@SendTo
	public String lowerCaseIt(String in) {
		return in.toLowerCase();
	}

	private String sendWithConfirms() throws Exception {
		CorrelationData data = new CorrelationData();
		String result = (String) this.confirmTemplate.convertSendAndReceive("", "cr2", "TWO", data);
		data.getFuture().get(10, TimeUnit.SECONDS);
		return result;
	}

	@Bean
	public Queue queue1() {
		return new Queue("cr1");
	}

	@Bean
	public Queue queue2() {
		return new Queue("cr2");
	}

	@Bean
	public ApplicationRunner runner(@Qualifier("template") RabbitTemplate template) {
		return args -> {
			System.out.println("++++++ Received: " + template.convertSendAndReceive("", "cr1", "one"));
		};
	}

}

@Configuration
class Templates {

	@Bean
	RabbitTemplate template(CachingConnectionFactory ccf) {
		return new RabbitTemplate(ccf);
	}

	@Bean("confirmTemplate")
	RabbitTemplate confirmTemplate(CachingConnectionFactory ccf) {
		CachingConnectionFactory confirmCF = new CachingConnectionFactory(ccf.getRabbitConnectionFactory());
		confirmCF.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
		return new RabbitTemplate(confirmCF);
	}

}