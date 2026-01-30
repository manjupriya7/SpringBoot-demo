package com.example.demo.service;

import com.example.demo.event.UserEvent;
import com.example.demo.event.UserEventProducer;
import com.example.demo.exception.DuplicateEmailException;
import com.example.demo.exception.InvalidUserDataException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;


    public UserService(UserRepository userRepository, UserEventProducer userEventProducer) {
        this.userRepository = userRepository;
        this.userEventProducer = userEventProducer;
    }

    public User createUser(User user) {
        validateUser(user);
        try {
            User userToSave = new User();
            userToSave.setName(user.getName());
            userToSave.setEmail(user.getEmail());
            userToSave.setBalance(user.getBalance());
            System.out.println("hey user");


            /*userEventProducer.sendUserEvent(
                    new UserEvent(
                            "USER_CREATED",
                            savedUser.getUserId(),
                            savedUser.getEmail(),
                            savedUser.getBalance(),
                            LocalDateTime.now()
                    )
            );*/

            return userRepository.save(userToSave);


        } catch (DataIntegrityViolationException ex) {
            log.error("exception is : ", ex);
            throw new DuplicateEmailException("Email already exists");
        }
    }
    @Cacheable(value ="users",key="#userId")
    public User getUser(UUID userId) {
        System.out.println("fetching from db");
        return userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User not found with id:"+userId));
    }
    //@CachePut(value ="users",key="#userId")
    public String updateUser(UUID userId,User updatedUser){
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        //User existingUser=userRepository.findById(userId).orElseThrow(()->new UserNotFoundException("User not found with id:"+userId));
        //publish(updateMsg);
        userEventProducer.sendUserEvent(
                new UserEvent(
                        "USER_UPDATE_REQUESTED",
                        userId,
                        updatedUser.getName(),
                        updatedUser.getEmail(),
                        updatedUser.getBalance(),
                        LocalDateTime.now()
                )
        );
//        if (updatedUser.getName() != null && !updatedUser.getName().isBlank()) {
//            existingUser.setName(updatedUser.getName());
//        }
//        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isBlank()) {
//            existingUser.setEmail(updatedUser.getEmail());
//        }
//        if (updatedUser.getBalance() != null && updatedUser.getBalance() >= 0) {
//            existingUser.setBalance(updatedUser.getBalance());
//        }
//        try {
//            return userRepository.save(existingUser);
//        } catch (DataIntegrityViolationException ex) {
//            throw new DuplicateEmailException("Email already exists");
//        }
        return "Your details will be updated shortly";
    }
    @CacheEvict(value ="users",key="#userId")
    public void deleteUser(UUID userId){
        if(!userRepository.existsById(userId)){
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteById(userId);
    }

    private void validateUser(User user) {

        if (user.getName() == null || user.getName().isBlank()) {
            throw new InvalidUserDataException("Name cannot be empty");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new InvalidUserDataException("Email cannot be empty");
        }
        if (user.getBalance() == null || user.getBalance() < 0) {
            throw new InvalidUserDataException("Balance must be zero or positive");
        }
    }

}
