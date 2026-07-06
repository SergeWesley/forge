package com.sergewesley.forge.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue"); // Added /queue for user destinations
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .setHandshakeHandler(
                        new org.springframework.web.socket.server.support
                                .DefaultHandshakeHandler() {
                            @Override
                            protected java.security.Principal determineUser(
                                    org.springframework.http.server.ServerHttpRequest request,
                                    org.springframework.web.socket.WebSocketHandler wsHandler,
                                    java.util.Map<String, Object> attributes) {
                                return new java.security.Principal() {
                                    private final String name =
                                            java.util.UUID.randomUUID().toString();

                                    @Override
                                    public String getName() {
                                        return this.name;
                                    }
                                };
                            }
                        });
    }
}
