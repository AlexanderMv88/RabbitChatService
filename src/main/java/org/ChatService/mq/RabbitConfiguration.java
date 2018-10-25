package org.ChatService.mq;



import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.ChatService.mq.RabbitEmployee.*;
import static org.ChatService.mq.RabbitMsg.*;


@Configuration
public class RabbitConfiguration {



    @Autowired
    private Environment env;

    @Bean
    public ConnectionFactory connectionFactory() {
//Для виртуалки
/*        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory("localhost");

        connectionFactory.setUsername("admin");
        connectionFactory.setPassword("admin");*/

            CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory("192.168.1.27");
            connectionFactory.setUsername("admin");
            connectionFactory.setPassword("admin");




        String hostname = "Unknown";

        try
        {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            hostname = addr.getHostName();
            System.out.println("!!!!!!!!!!!!hostname = " + hostname);
            String path = env.getProperty(hostname+".path");
            System.out.println("!!!!!!!!!!!!hostname.path = " + path);
        }
        catch (UnknownHostException ex)
        {
            System.out.println("Hostname can not be resolved");
        }



        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }


    //Создается exchange. Для отправки состояния
    @Bean
    public FanoutExchange fromServiceEmployeeFanoutExchange(){
        return new FanoutExchange(FROM_SERVICE_EMPLOYEE_FANOUT_EXCHANGE);
    }


    //Для получения запросов
    @Bean
    public Queue toServiceEmployeeEventQueue() {
        return new Queue(TO_SERVICE_EMPLOYEE_EVENT_QUEUE);
    }
    //На самом деле он уже должен быть создан тем, кто в это exchange будет отправлять сообщения. Сделано для того, чтобы очередь привязать к этому exchange
    @Bean
    public FanoutExchange toServiceEmployeeFanoutExchange(){
        return new FanoutExchange(TO_SERVICE_EMPLOYEE_FANOUT_EXCHANGE);
    }

    @Bean
    public Binding bindingEmployeeQueueToFanoutExchange(){
        return BindingBuilder.bind(toServiceEmployeeEventQueue()).to(toServiceEmployeeFanoutExchange());
    }








    //Создается exchange. Для отправки состояния
    @Bean
    public FanoutExchange fromServiceMsgFanoutExchange(){
        return new FanoutExchange(FROM_SERVICE_MSG_FANOUT_EXCHANGE);
    }
    //На самом деле она уже должна быть создана тем, кто в эту очередь будет отправлять сообщения. Сделано для того, чтобы пройти тесты
    @Bean
    public Queue fromServiceMsgEventQueue() {
        return new Queue(FROM_SERVICE_MSG_EVENT_QUEUE);
    }
    @Bean
    public Binding bindingFromMsgQueueToFanoutExchange(){
        return BindingBuilder.bind(fromServiceMsgEventQueue()).to(fromServiceMsgFanoutExchange());
    }



    //Для получения запросов
    @Bean
    public Queue toServiceMsgEventQueue() {
        return new Queue(TO_SERVICE_MSG_EVENT_QUEUE);
    }
    //На самом деле он уже должен быть создан тем, кто в это exchange будет отправлять сообщения. Сделано для того, чтобы очередь привязать к этому exchange
    @Bean
    public FanoutExchange toServiceMsgFanoutExchange(){
        return new FanoutExchange(TO_SERVICE_MSG_FANOUT_EXCHANGE);
    }

    @Bean
    public Binding bindingMsgQueueToFanoutExchange(){
        return BindingBuilder.bind(toServiceMsgEventQueue()).to(toServiceMsgFanoutExchange());
    }


}