package com.example.springboot;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import io.opentelemetry.extension.annotations.WithSpan;
//import com.example.model.DataRecord;
public class TestConsumer {
    private Properties _cfg;
    public Consumer<String, DataRecord> consumer;

    public TestConsumer() {
        try {
            this._cfg = this.loadConfig("/Users/sickles/.confluent/java.config");
            this._cfg.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            this._cfg.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonDeserializer");
            this._cfg.put(KafkaJsonDeserializerConfig.JSON_VALUE_TYPE, DataRecord.class);
            this._cfg.put(ConsumerConfig.GROUP_ID_CONFIG, "demo-consumer-1");
            this._cfg.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            this.consumer = new KafkaConsumer<String, DataRecord>(this._cfg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    
    @WithSpan
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
    @WithSpan
    public void subscribe(String topic) {
        this.consumer.subscribe(Arrays.asList(topic));
        Long total_count = 0L;
        Boolean keepconsuming = true;
        try {
        while (keepconsuming) {
            ConsumerRecords<String, DataRecord> records = consumer.poll(100);
            for (ConsumerRecord<String, DataRecord> record : records) {
            String key = record.key();
            DataRecord value = record.value();
            total_count += value.getCount();
            
            System.out.printf("Consumed record with key %s and value %s, and updated total count to %d%n", key, value,
                    total_count);
            if (total_count >= 5) {
                keepconsuming = false;
            }
            }
        }
        } finally {
            consumer.close();
        }
    }

    

    public void close() {
        this.consumer.close();
    }
}
