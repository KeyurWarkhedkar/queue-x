package com.keyur.queue_x.Services;

import com.keyur.queue_x.DTOs.EventDto;

public interface MessageQueue {

    void publish(String queueName, EventDto event);

    EventDto consume(String queueName);
}
