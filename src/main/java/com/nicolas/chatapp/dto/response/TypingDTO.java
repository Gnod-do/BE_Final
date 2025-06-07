package com.nicolas.chatapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypingDTO {
    private UUID chatId;
    private UUID senderId;
    private String senderName;
    private String type;
}
