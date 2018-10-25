package org.ChatService.msg;

import org.ChatService.EmployeeApiApplication;
import org.ChatService.entity.Employee;
import org.ChatService.entity.Msg;
import org.ChatService.repository.EmployeeRepository;
import org.ChatService.repository.MsgRepository;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmployeeApiApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MsgRepositoryTests {
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    MsgRepository msgRepository;

    @Test
    public void test1JpaCreate() throws InterruptedException {
        //Insert
        Stream.of(new Employee("Alexander"), new Employee("Nikita"), new Employee("Alesya"))
                .forEach(employee -> {
                    employeeRepository.save(employee);
                });
        Thread.sleep(100);
        List<Employee> employees = employeeRepository.findAll();
        assertThat(employees != null).isTrue();
        assertThat(employees.size() == 3).isTrue();
        Stream.of(new Msg("hi!", employees.get(0)), new Msg("HI", employees.get(1)), new Msg("hi)", employees.get(2)))
                .forEach(msg -> {
                    msgRepository.save(msg);
                });

        Thread.sleep(100);
        List<Msg> messages  = msgRepository.findAll();
        assertThat(messages != null).isTrue();
        assertThat(messages.size() == 3).isTrue();

    }

    @Test
    public void test2JpaFind() {
        //Select
        Employee employee = employeeRepository.findByFullName("Alexander").get(0);
        assertThat(employee.getFullName()).isEqualTo("Alexander");
        List<Msg> messages = msgRepository.findBySender(employee);
        assertThat(messages.size() == 1).isTrue();
        assertThat(messages.get(0).getText().equals("hi!")).isTrue();
    }


    @Test
    public void test3JpaChange() {
        //Update
        Employee employee = employeeRepository.findByFullName("Alexander").get(0);
        employee.setFullName("AlexanderMv");
        employeeRepository.save(employee);
        Employee changedEmployee = employeeRepository.findByFullName("AlexanderMv").get(0);
        assertThat(changedEmployee).isEqualTo(employee);
    }

    @Test
    public void test4JpaDelete() {
        List<Employee> chatUsersForDelete = employeeRepository.findAll();
        employeeRepository.deleteAll(chatUsersForDelete);
        assertThat(employeeRepository.findAll().size() == 0).isTrue();
    }
}
