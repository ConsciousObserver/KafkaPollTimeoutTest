package com.example.demo;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.Uuid;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class KafkaPollTimeoutApplication {

    private static final String MY_TOPIC_NAME = "my-topic";

    private static final String CONSUMER_GROUP_ID = "default";

    private static final int MESSAGE_COUNT = 100;

    public static void main(String[] args) {
        SpringApplication.run(KafkaPollTimeoutApplication.class, args);
    }

    @Bean
    NewTopic myTopic() {
        return TopicBuilder.name(MY_TOPIC_NAME).build();
    }

    /**
     * Sends messages to the topic at the start of the application. These
     * messages are then consumed by method annotated with @KafkaListener
     * 
     * @param kafkaTemplate
     * @return
     */
    @Bean
    ApplicationRunner runner(KafkaTemplate<String, String> kafkaTemplate) {
        return args -> {
            new Thread(() -> {
                // Wait for KafkaListener to be ready before sending messages to the topic
                sleep(5, true);

                for (int i = 0; i < MESSAGE_COUNT; i++) {
                    var message = "Hello " + i;

                    kafkaTemplate.send(MY_TOPIC_NAME, Uuid.randomUuid().toString(), message);
                }
                log.info("Sent {} message to {}", MESSAGE_COUNT, MY_TOPIC_NAME);
            }).start();
        };
    }

    /***
     * Listener that consumes messages from specified topic.
     * 
     * @param message
     */
    @KafkaListener(topics = MY_TOPIC_NAME, groupId = CONSUMER_GROUP_ID)
    void processMessage(String message) {
        log.info("################################# Received: {}", message);
        /* Max poll records has been set to 5 messages and poll time out is set to 10 seconds in the properties. 
         * That means this consumer when given 5 messages has 10 seconds to process them. If we sleep for 3 seconds 
         * for each message, then it would take at least 15 seconds to process 5 messages, inducing poll timeout*/
        sleep(3, false);
    }

    /**
     * Convenience sleep method that handles InterruptedException
     * 
     * @param seconds
     * @param logMessage
     */
    private void sleep(int seconds, boolean logMessage) {
        try {
            if (logMessage) {
                log.info("Sleeping");
            }
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            log.warn("Thread was interrupted");

            Thread.currentThread().interrupt();
        }
    }
}
