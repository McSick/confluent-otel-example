package com.kafkaexample.demo;
import io.opentelemetry.extension.annotations.WithSpan;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class ConsumerController {

	@RequestMapping("/")
	public String index(@RequestParam(required=false,defaultValue="1") Long numMessages) {
		this.consume(numMessages);
		return "Consume";
	}
	@WithSpan
	public void consume(Long numMessages) {
		TestConsumer consumer = new TestConsumer();
		String[] topics = {"Topic1", "Topic2", "Topic3"};
		consumer.subscribe(topics, numMessages);
	}

}
