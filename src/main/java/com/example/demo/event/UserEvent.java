package com.example.demo.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent {

    private String eventType;
    private UUID userId;
    private String name;
    private String email;
    private Long balance;

    private LocalDateTime eventTime;
}
