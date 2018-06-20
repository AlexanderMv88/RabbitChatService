package org.ChatService.mq;

public class RabbitEmployee {


    public static final String EMPLOYEE_SELECT_EVENT = "employee-select-event";
    public static final String EMPLOYEE_DELETE_EVENT = "employee-delete-event";
    public static final String EMPLOYEE_CREATE_EVENT = "employee-create-event";
    public static final String EMPLOYEE_UPDATE_EVENT = "employee-update-event";


    public static final String EMPLOYEE_CREATED_EVENT = "employee-created-event";
    public static final String EMPLOYEE_DELETED_EVENT = "employee-deleted-event";
    public static final String EMPLOYEE_UPDATED_EVENT = "employee-updated-event";

    public static final String FROM_SERVICE_EMPLOYEE_EVENT_QUEUE = "from-service-employee-event-queue";
    public static final String FROM_SERVICE_EMPLOYEE_FANOUT_EXCHANGE = "from-service-employee-fanout-exchange";

    public static final String TO_SERVICE_EMPLOYEE_EVENT_QUEUE = "to-service-employee-event-queue";
    public static final String TO_SERVICE_EMPLOYEE_FANOUT_EXCHANGE = "to-service-employee-fanout-exchange";
}
