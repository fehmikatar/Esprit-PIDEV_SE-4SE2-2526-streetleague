package tn.esprit._4se2.pi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String[] ALLOWED_ORIGINS = {
        "http://localhost:4200",
        "http://127.0.0.1:4200"
    };

    @Autowired
    private WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Value("${app.websocket.broker.mode:simple}")
    private String brokerMode;

    @Value("${app.websocket.relay.host:localhost}")
    private String relayHost;

    @Value("${app.websocket.relay.port:61613}")
    private Integer relayPort;

    @Value("${app.websocket.relay.client-login:guest}")
    private String relayClientLogin;

    @Value("${app.websocket.relay.client-passcode:guest}")
    private String relayClientPasscode;

    @Value("${app.websocket.relay.system-login:guest}")
    private String relaySystemLogin;

    @Value("${app.websocket.relay.system-passcode:guest}")
    private String relaySystemPasscode;

    @Value("${app.websocket.relay.virtual-host:/}")
    private String relayVirtualHost;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");

        if ("relay".equalsIgnoreCase(brokerMode)) {
            config.enableStompBrokerRelay("/topic", "/queue", "/user")
                    .setRelayHost(relayHost)
                    .setRelayPort(relayPort)
                    .setClientLogin(relayClientLogin)
                    .setClientPasscode(relayClientPasscode)
                    .setSystemLogin(relaySystemLogin)
                    .setSystemPasscode(relaySystemPasscode)
                    .setVirtualHost(relayVirtualHost);
            return;
        }

        config.enableSimpleBroker("/topic", "/queue", "/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
            .addEndpoint("/ws")
            .setAllowedOriginPatterns(ALLOWED_ORIGINS)
            .withSockJS();

        registry
            .addEndpoint("/ws-chat")
            .setAllowedOriginPatterns(ALLOWED_ORIGINS)
            .withSockJS();
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry
            .setMessageSizeLimit(128 * 1024)
            .setSendTimeLimit(15 * 1000)
            .setSendBufferSizeLimit(512 * 1024);
    }

    // ← AJOUT : enregistre l'interceptor JWT pour les connexions STOMP
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketAuthInterceptor);
    }
}