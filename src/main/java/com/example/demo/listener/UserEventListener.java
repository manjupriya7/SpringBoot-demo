package com.example.demo.listener;

import com.example.demo.event.UserEvent;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserEventListener {

    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    public UserEventListener(UserRepository userRepository, CacheManager cacheManager) {
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
    }

    @KafkaListener(
            topics = "user-events",
            groupId = "user-group-app",
            containerFactory="kafkaListenerContainerFactory"
    )
    public void consume(UserEvent event) {


        if (!"USER_UPDATE_REQUESTED".equals(event.getEventType())) {
            return;
        }

        //log.info("Processing async update for userId={}", event.getUserId());

        User user = userRepository.findById(event.getUserId())
                .orElseThrow();

        if (event.getName() != null) {
            user.setName(event.getName());
        }
        if (event.getEmail() != null) {
            user.setEmail(event.getEmail());
        }
        if (event.getBalance() != null) {
            user.setBalance(event.getBalance());
        }

        userRepository.save(user);

        cacheManager.getCache("users").evict(event.getUserId());



        log.info("User {} updated successfully", event.getUserId());
    }
}
