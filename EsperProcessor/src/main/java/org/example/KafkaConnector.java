package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


public class KafkaConnector {

    private static final KafkaConnector instance = new KafkaConnector();
    private final Logger logger = LoggerFactory.getLogger(KafkaConnector.class);

    public static KafkaConnector getInstance() {
        return instance;
    }

    private Connection connection;
    private Session session;
    private KafkaProducer<String, String> producer;

    private KafkaConnector() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "172.17.0.3:9092");
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 16384);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 33554432);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producer = new KafkaProducer<String, String>(properties);
    }

    public Consumer<String, String> getConsumer(String consumerGroup) throws Exception {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.17.0.3:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, /*consumerGroup*/ "group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        // receive messages that were sent before the consumer started
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // create the consumer using props.
        return new KafkaConsumer<>(props);
    }

    public void close() throws JMSException {
        producer.close();
        producer = null;
        session.close();
        session = null;
        connection.close();
        connection = null;
    }
}
