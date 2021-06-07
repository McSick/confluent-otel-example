package com.kafkaexample.demo;


import java.util.Properties;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.io.IOException;
import java.net.URI;
import io.opentelemetry.extension.annotations.WithSpan;

@RestController
public class ProducerController {

	@RequestMapping("/")
	public String index() {
		String topic = "Topic1";
		long numMessages = 1;
		this.produce(topic, numMessages);
		this.devour(numMessages);

		return "Greetings from Server1!";
	}

	@RequestMapping("/Topic1")
	public String topic1(@RequestParam(required=false,defaultValue="1") Long numMessages) {
		String topic = "Topic1";
		this.produce(topic, numMessages);
		this.devour(numMessages);

		return "Greetings from Server1!";
	}
	@RequestMapping("/Topic2")
	public String topic2(@RequestParam(required=false,defaultValue="1") Long numMessages) {
		String topic = "Topic2";
		this.produce(topic, numMessages);
		this.devour(numMessages);

		return "Greetings from Server1!";
	}
	@RequestMapping("/Topic3")
	public String topic3(@RequestParam(required=false,defaultValue="1") Long numMessages) {
		String topic = "Topic3";
		this.produce(topic, numMessages);
		this.devour(numMessages);

		return "Greetings from Server1!";
	}
	@WithSpan
	private void produce(String topic,long numMessages) {
		TestProducer tp = new TestProducer();
		tp.sendNMessages(topic, numMessages);
		tp.close();
	}
	private void devour(long numMessages) {
		// create a client
		HttpClient client = HttpClient.newHttpClient();

		// create a request
		HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:"+ System.getProperty("consumer_port") +"/?numMessages=" + String.valueOf(numMessages)))
				.header("accept", "application/json").build();
		try {
			// use the client to send the request
			HttpResponse<Void> response = client.send(request, BodyHandlers.discarding());
		} catch (Exception e) {
			System.out.println("Problem sending consumer request");
		}

		// the response:
		// System.out.println(response.body());
	}

}
