package com.nicolas.chatapp.config;

import com.nicolas.chatapp.model.UserStatus;
import com.nicolas.chatapp.repository.UserRepository;
import com.nicolas.chatapp.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;
    private final UserStatusRepository userStatusRepository;

    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event){
        try {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
            Principal principal = headerAccessor.getUser();

            System.out.println(principal);

            String email = principal.getName();
            UUID id = userRepository.getIdByEmail(email);
            String userId = id.toString();

//            String userId = headerAccessor.getUser().getName();

            //Update or create UserStatus
            Optional<UserStatus> userStatus = userStatusRepository.findById(userId);
            if (userStatus.isPresent()){
                UserStatus status = userStatus.get();
                status.setOnline(true);
                status.setLastActivityAt(Instant.now());
                userStatusRepository.save(status);
            }

            messagingTemplate.convertAndSend("/topic/users/online", userId);
            logger.info("User {} connected", userId);

        } catch (Exception e) {
            logger.error("Error handling WebSocket connect event:", e);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = headerAccessor.getUser();

        if (principal != null) {
            String email = principal.getName();

            try {
                UUID id = userRepository.getIdByEmail(email);
                String userId = id.toString();

                // Cập nhật trạng thái người dùng trong bảng user_status
                Optional<UserStatus> optionalUserStatus = userStatusRepository.findById(userId);
                if (optionalUserStatus.isPresent()) {
                    UserStatus userStatus = optionalUserStatus.get();
                    userStatus.setOnline(false);
                    userStatus.setLastActivityAt(Instant.now());
                    userStatusRepository.save(userStatus);  // cập nhật DB

                    // Gửi thông báo cho các client về người dùng offline
                    messagingTemplate.convertAndSend("/topic/users/offline", userId);
                    logger.info("User {} disconnected and marked offline", userId);
                } else {
                    logger.warn("UserStatus not found for userId: {}", userId);
                }
            } catch (Exception e) {
                logger.error("Error processing disconnect event for user: {}", email, e);
            }
        } else {
            logger.warn("Principal is null in disconnect event");
        }
    }

}
