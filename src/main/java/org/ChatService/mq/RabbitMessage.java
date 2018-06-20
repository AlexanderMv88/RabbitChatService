package org.ChatService.mq;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;

public class RabbitMessage {
    public static Message createMessage(String action, String payload){
        return MessageBuilder.withBody(payload.getBytes())
                .setHeader("action", action)
                .build();

    }
}
