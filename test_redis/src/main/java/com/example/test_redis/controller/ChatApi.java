package com.example.test_redis.controller;

import com.example.test_redis.model.ChatUser;
import com.example.test_redis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChatApi {

    @Autowired
    private UserService userService;

    private String decodeName;

    @GetMapping("/getFriends")
    public ResponseEntity<List<ChatUser>> getFriends(@RequestHeader("userName") String userName) {
        try {
            decodeName = java.net.URLDecoder.decode(userName, "UTF-8");
            List<ChatUser> friends = userService.getFriends(decodeName);
            return ResponseEntity.ok()
                    .header("friends-count", String.valueOf(friends.size()))
                    .body(friends);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/chatRoom")
    public ResponseEntity idIsEmpty(@RequestHeader("userName") String userName) {
        try {
            decodeName = java.net.URLDecoder.decode(userName, "UTF-8");
            if(!userService.isEmpty(decodeName)){
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }else {
                return ResponseEntity.status(HttpStatus.OK).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
