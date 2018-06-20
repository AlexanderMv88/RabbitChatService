package org.ChatService.mq;

public class RabbitMsg {


    public static final String MSG_SELECT_EVENT = "msg-select-event";
    public static final String MSG_CREATE_EVENT = "msg-create-event";


    public static final String MSG_CREATED_EVENT = "msg-created-event";


    public static final String FROM_SERVICE_MSG_EVENT_QUEUE = "from-service-msg-event-queue";
    public static final String FROM_SERVICE_MSG_FANOUT_EXCHANGE = "from-service-msg-fanout-exchange";

    public static final String TO_SERVICE_MSG_EVENT_QUEUE = "to-service-msg-event-queue";
    public static final String TO_SERVICE_MSG_FANOUT_EXCHANGE = "to-service-msg-fanout-exchange";
}
