package org.ChatService.msg;

import org.ChatService.EmployeeApiApplication;
import org.ChatService.entity.Employee;
import org.ChatService.entity.Msg;
import org.ChatService.mq.RabbitMqPublisher;
import org.ChatService.repository.EmployeeRepository;
import org.ChatService.repository.MsgRepository;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import static org.ChatService.mq.RabbitEmployee.*;
import static org.ChatService.mq.RabbitMessage.createMessage;
import static org.ChatService.mq.RabbitMsg.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmployeeApiApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
public class MsgAmqpTest {

    private HttpMessageConverter mappingJackson2HttpMessageConverter;


    private static boolean isCreated=false;

    //
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    MsgRepository msgRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }



    @Test
    public void test1Create() throws Exception {

        rabbitTemplate.setExchange(TO_SERVICE_EMPLOYEE_FANOUT_EXCHANGE);
        String jsonObj = json(new Employee("Дима"));
        Message message = createMessage(EMPLOYEE_CREATE_EVENT, jsonObj);

        String jsonObj1 = json(new Employee("Саня"));
        Message message1 = createMessage(EMPLOYEE_CREATE_EVENT, jsonObj1);

        rabbitTemplate.send(message);
        rabbitTemplate.send(message1);

        Thread.sleep(100);

        List<Employee> employees = employeeRepository.findAll();
        assertThat(employees != null).isTrue();
        assertThat(employees.size() == 2).isTrue();
        assertThat(employees.get(0).getFullName().equals("Дима")).isTrue();
        assertThat(employees.get(1).getFullName().equals("Саня")).isTrue();





        rabbitTemplate.setExchange(TO_SERVICE_MSG_FANOUT_EXCHANGE);
        String jsonMsg = json(new Msg("Hi, I am batman!", employees.get(0)));
        Message message2 = createMessage(MSG_CREATE_EVENT, jsonMsg);

        String jsonMsg1 = json(new Msg("Hi, batman!", employees.get(1)));
        Message message3 = createMessage(MSG_CREATE_EVENT, jsonMsg1);

        rabbitTemplate.send(message2);
        rabbitTemplate.send(message3);


        Thread.sleep(100);

        List<Msg> messages = msgRepository.findAll();
        assertThat(messages != null).isTrue();
        assertThat(messages.size() == 2).isTrue();
        assertThat(messages.get(0).getText().equals("Hi, I am batman!")).isTrue();
        assertThat(messages.get(0).getSender().equals(employees.get(0))).isTrue();

        assertThat(messages.get(1).getText().equals("Hi, batman!")).isTrue();
        assertThat(messages.get(1).getSender().equals(employees.get(1))).isTrue();





    }

//    @Test
//    public void test2Update() throws Exception {
//        Employee oldEmployee = employeeRepository.findByFullName("Дима").get(0);
//        //String oldJsonObj = json(oldEmployee);
//
//        Employee newEmployee = new Employee(oldEmployee);
//        newEmployee.setFullName("Диман");
//        List<Employee> employees = new ArrayList<Employee>();
//        employees.add(oldEmployee);
//        employees.add(newEmployee);
//        //String newJsonObj = json(newEmployee);
//        String jsonObjs = json(employees);
//
//
//        Message msg = createMessage(EMPLOYEE_UPDATE_EVENT, jsonObjs);
//        rabbitTemplate.setExchange(TO_SERVICE_EMPLOYEE_FANOUT_EXCHANGE);
//        rabbitTemplate.send(msg);
//
//        Thread.sleep(100);
//
//        List<Employee> employeesFromDB = employeeRepository.findByFullName("Диман");
//        assertThat(employeesFromDB != null).isTrue();
//        assertThat(employeesFromDB.size() == 1).isTrue();
//        assertThat(employeesFromDB.get(0).getFullName().equals("Диман")).isTrue();
//
//
//    }
//
//
//    @Test
//    public void test3Remove() throws Exception {
//        String jsonObj = json(new Employee("Диман"));
//
//        Message msg = createMessage(EMPLOYEE_DELETE_EVENT, jsonObj);
//        rabbitTemplate.setExchange(TO_SERVICE_EMPLOYEE_FANOUT_EXCHANGE);
//        rabbitTemplate.send(msg);
//
//        Thread.sleep(100);
//
//        List<Employee> employees = employeeRepository.findByFullName("Дима");
//        //assertThat(employees == null).isTrue();
//        assertThat(employees.size() == 0).isTrue();
//        //assertThat(employees.get(0).getFullName().equals("Диман")).isTrue();
//
//
//    }







    @RabbitListener(queues = FROM_SERVICE_MSG_EVENT_QUEUE)
    public void fromServiceEmployee(Message message){
        System.out.println("get message "+message);
        String action = (String) message.getMessageProperties().getHeaders().get("action");
        String body = null;
        try {
            body = new String(message.getBody(), "UTF-8");
            if (MSG_CREATED_EVENT.equals(action)) {
                isCreated=true;

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


    @Test
    public void test4CheckListenResults(){

        assertThat(isCreated).isTrue();




    }


}
