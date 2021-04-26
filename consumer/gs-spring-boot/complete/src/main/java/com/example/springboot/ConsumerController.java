package com.example.springboot;
import io.opentelemetry.extension.annotations.WithSpan;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class ConsumerController {

	@RequestMapping("/")
	public String index() {
		this.consume();
		return "Consume";
	}
	@WithSpan
	public void consume() {
		TestConsumer consumer = new TestConsumer();
		consumer.subscribe("testing");
	}

}
