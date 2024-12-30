package com.example.test_redis.service;

import com.example.test_redis.model.ChatUser;
import com.example.test_redis.repository.ChatUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private ChatUserRepository chatUserRepository;

    public List getFriends(String namePattern) {
        List<ChatUser> chatUsers = chatUserRepository.findAll();
        chatUsers = chatUsers.stream().filter(chatUser -> !namePattern.equals(chatUser.getName())).collect(Collectors.toList());
        return chatUsers;
    }

    public void addFriend(String sessionId, String name) {
        chatUserRepository.save(new ChatUser(sessionId ,name));
    }

    public boolean isEmpty(String name) {
        return Objects.isNull(chatUserRepository.findByName(name));
    }
    public void deleteFriend(String sessionId) {
        chatUserRepository.deleteById(sessionId);
    }
    public Optional<ChatUser> getFriend(String sessionId) {
        return chatUserRepository.findById(sessionId);
    }
}
