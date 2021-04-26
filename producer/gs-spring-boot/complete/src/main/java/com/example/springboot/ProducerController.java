package com.example.springboot;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
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
		this.produce();
		this.devour();

		return "Greetings from Server1!";
	}
	@WithSpan
	private void produce() {
		TestProducer tp = new TestProducer();
		long numMessages = 5;
		tp.sendNMessages("testing", numMessages);
		tp.close();
	}
	private void devour() {
		// create a client
		HttpClient client = HttpClient.newHttpClient();

		// create a request
		HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8081/"))
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
