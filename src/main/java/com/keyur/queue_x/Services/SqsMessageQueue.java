package com.keyur.queue_x.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyur.queue_x.DTOs.EventDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class SqsMessageQueue implements MessageQueue {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final QueueConstants queueConstants;

    @Override
    public void publish(String queueName, EventDto event) {
        try {
            String queueUrl = queueConstants.urlMap().get(queueName);
            String body = objectMapper.writeValueAsString(event);

            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(body)
                    .build());

            log.info("Published to SQS queue={}", queueName);

        } catch (Exception e) {
            throw new RuntimeException("Failed to publish to SQS: " + queueName, e);
        }
    }

    @Override
    public EventDto consume(String queueName) {
        try {
            String queueUrl = queueConstants.urlMap().get(queueName);

            List<Message> messages = sqsClient.receiveMessage(
                    ReceiveMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .maxNumberOfMessages(1)
                            .waitTimeSeconds(0) // non-blocking, matches your @Scheduled polling
                            .build()
            ).messages();

            if (messages.isEmpty()) return null;

            Message message = messages.get(0);

            // Delete from SQS immediately after receiving
            sqsClient.deleteMessage(DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(message.receiptHandle())
                    .build());

            return objectMapper.readValue(message.body(), EventDto.class);

        } catch (Exception e) {
            log.error("Failed to consume from SQS queue={}", queueName, e);
            return null;
        }
    }
}