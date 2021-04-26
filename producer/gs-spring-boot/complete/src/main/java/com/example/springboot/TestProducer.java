package com.example.springboot;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.Producer;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import io.opentelemetry.extension.annotations.WithSpan;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.common.errors.TopicExistsException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
//import com.example.model.DataRecord;
public class TestProducer {
    private Properties _cfg;
    public Producer<String, DataRecord> producer;

    public TestProducer() {
        try {
            this._cfg = this.loadConfig("~/.confluent/java.config");
            this._cfg.put(ProducerConfig.ACKS_CONFIG, "all");
            this._cfg.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                    "org.apache.kafka.common.serialization.StringSerializer");
            this._cfg.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                    "io.confluent.kafka.serializers.KafkaJsonSerializer");
            this.producer = new KafkaProducer<String, DataRecord>(this._cfg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    // Create topic in Confluent Cloud
    @WithSpan
    public void createTopic(final String topic) {
        int partitions = 1;
        short replicationFactor = 1;
        final NewTopic newTopic = new NewTopic(topic, partitions, replicationFactor);
        try (final AdminClient adminClient = AdminClient.create(this._cfg)) {
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
        } catch (final InterruptedException | ExecutionException e) {
            // Ignore if TopicExistsException, which may be valid if topic exists
            if (!(e.getCause() instanceof TopicExistsException)) {
                throw new RuntimeException(e);
            }
        }
    }

    public Properties loadConfig(final String configFile) throws IOException {
        if (!Files.exists(Paths.get(configFile))) {
            throw new IOException(configFile + " not found.");
        }
        final Properties cfg = new Properties();
        try (InputStream inputStream = new FileInputStream(configFile)) {
            cfg.load(inputStream);
        }
        return cfg;
    }

    public void sendNMessages(String topic, Long numMessages) {
        this.createTopic(topic);
        for (Long i = 0L; i < numMessages; i++) {
            String key = "alice";
            DataRecord record = new DataRecord(i);

            System.out.printf("Producing record: %s\t%s%n", key, record);
            this.producer.send(new ProducerRecord<String, DataRecord>(topic, key, record), new Callback() {
                @Override
                public void onCompletion(RecordMetadata m, Exception e) {
                    if (e != null) {
                    e.printStackTrace();
                    } else {
                    System.out.printf("Produced record to topic %s partition [%d] @ offset %d%n", m.topic(), m.partition(), m.offset());
                    }
                }
            });
        }

        producer.flush();

        System.out.printf("10 messages were produced to topic %s%n", topic);
    }

    public void close() {
        this.producer.flush();
        this.producer.close();
    }
}
