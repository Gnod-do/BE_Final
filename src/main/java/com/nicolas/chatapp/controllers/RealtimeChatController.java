package com.nicolas.chatapp.controllers;

import com.nicolas.chatapp.dto.response.TypingDTO;
import com.nicolas.chatapp.model.Message;
import com.nicolas.chatapp.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RealtimeChatController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/messages")
    public void receiveMessage(@Payload Message message) {

        messagingTemplate.convertAndSend("/topic/" + message.getChat().getId(), message);

        for (User user : message.getChat().getUsers()) {
            final String destination = "/topic/" + user.getId();
            messagingTemplate.convertAndSend(destination, message);
        }
    }


    @MessageMapping("/typing")
    public void handleTyping(@Payload TypingDTO typing) {
        messagingTemplate.convertAndSend("/topic/typing/" + typing.getChatId(), typing);
        log.info("Send typing to server");
    }

}
