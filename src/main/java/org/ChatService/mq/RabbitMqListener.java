package org.ChatService.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.ChatService.entity.Employee;
import org.ChatService.entity.Msg;
import org.ChatService.repository.EmployeeRepository;
import org.ChatService.repository.MsgRepository;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.ChatService.mq.RabbitEmployee.*;
import static org.ChatService.mq.RabbitMsg.MSG_CREATE_EVENT;
import static org.ChatService.mq.RabbitMsg.MSG_SELECT_EVENT;
import static org.ChatService.mq.RabbitMsg.TO_SERVICE_MSG_EVENT_QUEUE;

@EnableRabbit //нужно для активации обработки аннотаций @RabbitListener
@Component
public class RabbitMqListener {
    //Logger logger = Logger.getLogger(RabbitMqListener.class);

    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    MsgRepository msgRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

/*
    @RabbitListener(queues = EMPLOYEE_CREATED_QUEUE)
    public void employeeCreated(String message) {
        //logger.info("Received from queue 1: " + message);
        System.out.println("Received from "+ EMPLOYEE_CREATED_QUEUE +": " + message);
    }

    @RabbitListener(queues = EMPLOYEE_UPDATED_QUEUE)
    public void employeeUpdated(String message) {
        //logger.info("Received from queue 1: " + message);
        System.out.println("Received from "+ EMPLOYEE_UPDATED_QUEUE +": " + message);
    }

    @RabbitListener(queues = EMPLOYEE_DELETED_QUEUE)
    public void employeeDeleted(String message) {
        //logger.info("Received from queue 1: " + message);
        System.out.println("Received from "+ EMPLOYEE_DELETED_QUEUE +": " + message);
    }

*/
/*
    @RabbitListener(queues = EMPLOYEE_SELECT_QUEUE)
    public String selectAllEmployee() throws JsonProcessingException {
        List<Employee> employees = employeeRepository.findAll();
        String jsonEmployees =  new ObjectMapper().writeValueAsString(employees);
        return  jsonEmployees;
    }

    @RabbitListener(queues = EMPLOYEE_DELETE_QUEUE)
    public void removeEmployee(String message) throws JsonProcessingException {
        try {
            Employee employee =  new ObjectMapper().readValue(message, Employee.class);
            employeeRepository.delete(employee);
            if (!employeeRepository.findById(employee.getId()).isPresent()){
                try {
                    String jsonEmployeeForRemove= new ObjectMapper().writeValueAsString(employee);
                    rabbitTemplate.setExchange("employee-deleted-fanout");
                    rabbitTemplate.convertAndSend(jsonEmployeeForRemove);
                } catch (JsonProcessingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    */

    @RabbitListener(queues = TO_SERVICE_EMPLOYEE_EVENT_QUEUE)
    public String getEmployeeRequests(Message message) throws JsonProcessingException {
        /*List<Employee> employees = employeeRepository.findAll();
        String jsonEmployees =  new ObjectMapper().writeValueAsString(employees);
        return  jsonEmployees;*/

        System.out.println("get message "+message);
        String action = (String) message.getMessageProperties().getHeaders().get("action");
        String body = null;
        try {
            body = new String(message.getBody(), "UTF-8");
            if (EMPLOYEE_SELECT_EVENT.equals(action)) {
                System.out.println("get message with action "+action+" "+body);
                List<Employee> employees = employeeRepository.findAll();
                String jsonEmployees =  new ObjectMapper().writeValueAsString(employees);
                return  jsonEmployees;


            }else if (EMPLOYEE_DELETE_EVENT.equals(action)) {
                System.out.println("get message with action "+action+" "+body);
                //return null;
                Employee employee =  new ObjectMapper().readValue(body, Employee.class);
                employeeRepository.delete(employee);
                if (!employeeRepository.findById(employee.getId()).isPresent()){
                    try {
                        new RabbitMqPublisher().sendDeletedEmployeeMessage(rabbitTemplate, employee);

                    } catch (JsonProcessingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }else if (EMPLOYEE_UPDATE_EVENT.equals(action)) {
                System.out.println("get message with action "+action+" "+body);
                ObjectMapper mapper = new ObjectMapper();
                CollectionType javaType = mapper.getTypeFactory()
                        .constructCollectionType(List.class, Employee.class);

                List<Employee> employees = mapper.readValue(body, javaType);
                //TODO: replace Employee to List<Employee>
                Employee oldEmployee = employees.get(0);
                Employee newEmployee = employees.get(1);

                Employee oldEmployeeFromDB = employeeRepository.findById(oldEmployee.getId()).get();
                oldEmployeeFromDB.setData(newEmployee);
                employeeRepository.save(oldEmployeeFromDB);
                new RabbitMqPublisher().sendUpdatedEmployeeMessage(rabbitTemplate, oldEmployee, newEmployee);
                //return null;
                /*Employee employee =  new ObjectMapper().readValue(body, Employee.class);
                employeeRepository.delete(employee);
                if (!employeeRepository.findById(employee.getId()).isPresent()){
                    try {
                        new RabbitMqPublisher().sendDeletedEmployeeMessage(rabbitTemplate, employee);

                    } catch (JsonProcessingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }*/
            }else if (EMPLOYEE_CREATE_EVENT.equals(action)) {
                System.out.println("get message with action "+action+" "+body);
                //return null;
                Employee employee =  new ObjectMapper().readValue(body, Employee.class);
                employeeRepository.save(employee);
                if (employeeRepository.findById(employee.getId()).isPresent()){
                    try {
                        new RabbitMqPublisher().sendCreatedEmployeeMessage(rabbitTemplate, employee);

                    } catch (JsonProcessingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    @RabbitListener(queues = TO_SERVICE_MSG_EVENT_QUEUE)
    public String getMsgRequests(Message message) throws JsonProcessingException {
        /*List<Employee> employees = employeeRepository.findAll();
        String jsonEmployees =  new ObjectMapper().writeValueAsString(employees);
        return  jsonEmployees;*/

        System.out.println("get message "+message);
        String action = (String) message.getMessageProperties().getHeaders().get("action");
        String body = null;
        try {
            body = new String(message.getBody(), "UTF-8");
            if (MSG_SELECT_EVENT.equals(action)) {
                System.out.println("get message with action "+action+" "+body);
                List<Msg> messages = msgRepository.findAll();
                String jsonMessages =  new ObjectMapper().writeValueAsString(messages);
                return  jsonMessages;


            }else if (MSG_CREATE_EVENT.equals(action)) {
                System.out.println("get message with action "+action+" "+body);
                //return null;
                Msg msg =  new ObjectMapper().readValue(body, Msg.class);
                msgRepository.save(msg);
                if (msgRepository.findById(msg.getId()).isPresent()){
                    try {
                        new RabbitMqPublisher().sendCreatedMsgMessage(rabbitTemplate, msg);

                    } catch (JsonProcessingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*private void sendDeletedEmployeeMessage(Employee employee) throws JsonProcessingException {
        String jsonEmployeeForRemove= new ObjectMapper().writeValueAsString(employee);
        Message msg = createMessage(EMPLOYEE_DELETED_EVENT, jsonEmployeeForRemove);
        rabbitTemplate.setExchange(FROM_SERVICE_EMPLOYEE_FANOUT_EXCHANGE);
        rabbitTemplate.send(msg);
    }*/
}

