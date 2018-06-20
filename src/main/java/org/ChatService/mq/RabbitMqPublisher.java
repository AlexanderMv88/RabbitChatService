package org.ChatService.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ChatService.entity.Employee;
import org.ChatService.entity.Msg;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.ChatService.mq.RabbitEmployee.*;
import static org.ChatService.mq.RabbitMessage.createMessage;
import static org.ChatService.mq.RabbitMsg.FROM_SERVICE_MSG_FANOUT_EXCHANGE;
import static org.ChatService.mq.RabbitMsg.MSG_CREATED_EVENT;

public class RabbitMqPublisher {


    public void sendDeletedEmployeeMessage(RabbitTemplate rabbitTemplate, Employee employee) throws JsonProcessingException {
        String jsonEmployeeForRemove= new ObjectMapper().writeValueAsString(employee);
        Message msg = createMessage(EMPLOYEE_DELETED_EVENT, jsonEmployeeForRemove);
        rabbitTemplate.setExchange(FROM_SERVICE_EMPLOYEE_FANOUT_EXCHANGE);
        rabbitTemplate.send(msg);
    }

    public void sendUpdatedEmployeeMessage(RabbitTemplate rabbitTemplate, Employee oldEmployee, Employee newEmployee) throws JsonProcessingException {
        List<Employee> employees = new ArrayList<Employee>();
        employees.add(oldEmployee);
        employees.add(newEmployee);
        String jsonEmployeeForUpdate= new ObjectMapper().writeValueAsString(employees);
        Message msg = createMessage(EMPLOYEE_UPDATED_EVENT, jsonEmployeeForUpdate);
        rabbitTemplate.setExchange(FROM_SERVICE_EMPLOYEE_FANOUT_EXCHANGE);
        rabbitTemplate.send(msg);
    }

    public void sendCreatedEmployeeMessage(RabbitTemplate rabbitTemplate, Employee employee) throws JsonProcessingException {
        String jsonEmployeeForInsert= new ObjectMapper().writeValueAsString(employee);
        Message msg = createMessage(EMPLOYEE_CREATED_EVENT, jsonEmployeeForInsert);
        rabbitTemplate.setExchange(FROM_SERVICE_EMPLOYEE_FANOUT_EXCHANGE);
        rabbitTemplate.send(msg);
    }

    public void sendCreatedMsgMessage(RabbitTemplate rabbitTemplate, Msg msg) throws JsonProcessingException {
        String jsonMsgForInsert= new ObjectMapper().writeValueAsString(msg);
        Message message = createMessage(MSG_CREATED_EVENT, jsonMsgForInsert);
        rabbitTemplate.setExchange(FROM_SERVICE_MSG_FANOUT_EXCHANGE);
        rabbitTemplate.send(message);
    }
}
