package org.example.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.*;
import jakarta.jms.Message;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.events.IEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static jakarta.jms.Session.AUTO_ACKNOWLEDGE;
import static org.apache.activemq.ActiveMQConnection.DEFAULT_BROKER_URL;

public class EventStoreService {

    private static final EventStoreService instance = new EventStoreService();
    private final Logger logger = LoggerFactory.getLogger(EventStoreService.class);

    public static EventStoreService getInstance() {
        return instance;
    }

    private static final String CLIENTID = "StreamingSystemsPublisher";
    private Connection connection;
    private Session session;
    private KafkaProducer<String, String> producer;
    private KafkaConsumer<String, String> consumer;

    private EventStoreService() {
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
        this.consumer = new KafkaConsumer<String, String>(properties);
    }

    public void raiseEvent(IEvent event) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(event);
        String topic = "events";
        producer.send(
                new ProducerRecord<String, String>(topic, json)
        );
        this.logger.debug("Message sent to the queue: {}", json);
    }

    public KafkaConsumer<String, String> getConsumer() throws Exception {
        return this.consumer;
    }

    public void close() throws JMSException {
        producer.close();
        producer = null;
        consumer.close();
        consumer = null;
        session.close();
        session = null;
        connection.close();
        connection = null;
    }
}
