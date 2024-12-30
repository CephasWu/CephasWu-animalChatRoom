package com.example.test_redis.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash("chatUser")  // Redis 中的 key 会使用这个值
public class ChatUser {

    @Indexed
    @Id
    private String id;

    @Indexed
    private String name;

    public ChatUser() {
    }

    public ChatUser(String sessionId, String name) {
        this.id = sessionId;
        this.name = name;
    }
}