package org.ChatService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ChatService.entity.Employee;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.core.Is.is;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmployeeApiApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
public class EmployeeRestTests {


    @Autowired
    private MockMvc mvc;


    private HttpMessageConverter mappingJackson2HttpMessageConverter;

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
    public void test1PostNotFound() throws Exception {
        String jsonObj = json(new Employee("Дима"));

        mvc.perform(post("/api/addUser")
               // .with(httpBasic("Alexander", "12345"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObj))
                .andExpect(status().isNotFound());
    }

    @Test
    public void test1_1PostUnprocEntity() throws Exception {
        String jsonObj = json(new Employee());

        mvc.perform(post("/api/addEmployee")
                //.with(httpBasic("Alexander", "12345"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObj))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void test2Post() throws Exception {
        String jsonObj = json(new Employee("Дима"));

        mvc.perform(post("/api/addEmployee")
                //.with(httpBasic("Alexander", "12345"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObj))
                .andExpect(status().isCreated());
    }



    @Test
    public void test3Get() throws Exception {
        //employeeRepository.save(new Employee("Дима"));

        mvc.perform(get("/api/findBy?fullName=Дима")
                //.with(httpBasic("Alexander", "12345"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].fullName", is("Дима")));


    }

    @Test
    public void test4PutNotFound() throws Exception {


        ResultActions res = mvc.perform(put("/api/changeUser/")
                //.with(httpBasic("Alexander", "12345"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void test4_1PutUnprocEntity() throws Exception {
        Employee employee = getByName("Дима");
        assertThat(employee.getFullName()).isEqualTo("Дима");

        employee.setFullName(null);
        String jsonObj = json(employee);

        mvc.perform(put("/api/changeEmployee/"+ employee.getId())
                //.with(httpBasic("Alexander", "12345"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObj))
                .andExpect(status().isUnprocessableEntity());
    }


    @Test
    public void test5Put() throws Exception {
        Employee employee = getByName("Дима");
        assertThat(employee.getFullName()).isEqualTo("Дима");

        employee.setFullName("Дмитрий Палыч");
        String jsonObj = json(employee);

        mvc.perform(put("/api/changeEmployee/"+ employee.getId())
                //.with(httpBasic("Alexander", "12345"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObj))
                .andExpect(status().isCreated());
    }


    @Test
    public void test6DeleteNotFound(){
        try {
            mvc.perform(delete("/api/deleteEmployee/333333333")
                    .with(httpBasic("Alexander", "12345"))
                .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            logout();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void logout() {
        SecurityContextHolder.clearContext();
    }

/*
    @Test
    public void test7SecurityFail() throws Exception {



        Employee employee = getByName("Дмитрий Палыч");
        assertThat(employee.getFullName()).isEqualTo("Дмитрий Палыч");

        try {
            mvc.perform(delete("/api/deleteEmployee/"+ employee.getId())
                    //.with(httpBasic("Alexander", "123"))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

    @Test
    public void test8Delete(){
        Employee employee = getByName("Дмитрий Палыч");
        assertThat(employee.getFullName()).isEqualTo("Дмитрий Палыч");

        try {
            mvc.perform(delete("/api/deleteEmployee/"+ employee.getId())
                    //.with(httpBasic("Alexander", "12345"))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private Employee getByName(String name) {

        MvcResult result = null;
        try {
            result = mvc.perform(get("/api/findBy?fullName=" + name)
                    //.with(httpBasic("Alexander", "12345"))
                    .contentType(MediaType.APPLICATION_JSON)).andReturn();

            String content = result.getResponse().getContentAsString();
            content = content.replace("[", "").replace("]", "");
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<Employee> typeReference = new TypeReference<Employee>() {

            };
            InputStream inputStream = new ByteArrayInputStream(content.getBytes());
            try {
                return mapper.readValue(inputStream, typeReference);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }


}
