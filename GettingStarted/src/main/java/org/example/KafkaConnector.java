package org.example;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;

// A simple connector demonstrating functionality of the kafka server
public class KafkaConnector {

    private final Producer<String, String> producer;

    KafkaConnector () {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "172.17.0.3:9092");
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 16384);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 33554432);
        properties.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        this.producer = new KafkaProducer<String, String>(properties);
    }

    public void sendSomeData() {
        String topicName = "testTopic";
        for (int i = 0; i < 10; i++) {
            this.producer.send(
                    new ProducerRecord<String, String>(topicName, "test", Integer.toString(i))
            );
        }
        this.producer.close();
    }
}
