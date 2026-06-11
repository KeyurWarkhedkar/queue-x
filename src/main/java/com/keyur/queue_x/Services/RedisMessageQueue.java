package com.keyur.queue_x.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyur.queue_x.DTOs.EventDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RedisMessageQueue implements MessageQueue {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(String queueName, EventDto event) {
        redisTemplate.opsForList()
                .rightPush(queueName, event);
    }

    @Override
    public EventDto consume(String queueName) {

        Object obj = redisTemplate.opsForList()
                .leftPop(queueName);

        if (obj == null) return null;

        return objectMapper.convertValue(obj, EventDto.class);
    }
}
