package com.example.test_redis.repository;

import com.example.test_redis.model.ChatUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatUserRepository extends CrudRepository<ChatUser, String> {
    List<ChatUser> findAll();
    ChatUser findByName(String name);
    Optional<ChatUser> findById(String id);
}
