package org.example.services;
import jakarta.jms.*;
import jakarta.jms.Message;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.example.events.IEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private MessageProducer producer;
    private MessageConsumer consumer;

    public EventStoreService() {
        try {
            String queueName = "StreamingSystemsQueue";
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                    "artemis",
                    "artemis",
                    "tcp://172.17.0.3:61616"
            );
            connection = connectionFactory.createConnection();
            connection.setClientID(CLIENTID);
            connection.start();
            session = connection.createSession(false, AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            producer = session.createProducer(destination);
            consumer = session.createConsumer(destination);
        } catch (JMSException e) {
            this.logger.error("Failed to connect to activemq: {}", e.getMessage());
        }
    }

    public void raiseEvent(IEvent event) throws Exception {
        // We will send a text message
        ObjectMessage message = session.createObjectMessage(event);
        // push the message into queue
        producer.send(message);
        this.logger.debug("Message sent to the queue: {}", message);
    }

    public MessageConsumer getConsumer() throws Exception {
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
