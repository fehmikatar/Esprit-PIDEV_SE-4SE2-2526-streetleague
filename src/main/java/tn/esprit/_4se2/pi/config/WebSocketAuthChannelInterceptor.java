package tn.esprit._4se2.pi.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.security.jwt.JwtService;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
            MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        String raw = accessor.getFirstNativeHeader("Authorization");
        if (raw == null || !raw.startsWith("Bearer ")) {
            return message;
        }

        try {
            String token = raw.substring(7);
            String email = jwtService.extractEmail(token);
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) return message;

            var principal = new UsernamePasswordAuthenticationToken(
                email, null,
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
            accessor.setUser(principal);

            Map<String, Object> attrs = accessor.getSessionAttributes();
            if (attrs != null) {
                attrs.put("userId", user.getId());
                attrs.put("userName",
                    (user.getFirstName() + " " + user.getLastName()).trim());
            }
        } catch (Exception e) {
            log.warn("WebSocket JWT validation failed: {}", e.getMessage());
        }

        return message;
    }
}
