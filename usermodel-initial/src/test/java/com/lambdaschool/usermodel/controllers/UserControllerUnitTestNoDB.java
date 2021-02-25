package com.lambdaschool.usermodel.controllers;

import com.lambdaschool.usermodel.UserModelApplicationTesting;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.models.UserRoles;
import com.lambdaschool.usermodel.models.Useremail;
import com.lambdaschool.usermodel.repository.RoleRepository;
import com.lambdaschool.usermodel.repository.UserRepository;
import com.lambdaschool.usermodel.services.HelperFunctions;
import com.lambdaschool.usermodel.services.RoleService;
import com.lambdaschool.usermodel.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WithMockUser(username = "admin",
        roles = {"USER", "ADMIN"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = UserModelApplicationTesting.class,
        properties = {
                "command.line.runner.enabled=false"})
@AutoConfigureMockMvc
public class UserControllerUnitTestNoDB {

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userrepos;

    @MockBean
    private RoleService roleService;

    @MockBean
    private RoleRepository rolerepos;

    @MockBean
    private HelperFunctions helperFunctions;

    private List<User> userList = new ArrayList<>();

    @Before
    public void setUp() throws Exception {


        Role r1 = new Role("admin");
        Role r2 = new Role("user");
        Role r3 = new Role("data");

        r1.setRoleid(1);
        r2.setRoleid(2);
        r3.setRoleid(3);


        r1 = roleService.save(r1);
        r2 = roleService.save(r2);
        r3 = roleService.save(r3);

        // admin, data, user
        User u1 = new User("admin test",
                "password",
                "admin@lambdaschool.local");

        u1.setUserid(1);

        u1.getRoles()
                .add(new UserRoles(u1,
                        r1));
        u1.getRoles()
                .add(new UserRoles(u1,
                        r2));
        u1.getRoles()
                .add(new UserRoles(u1,
                        r3));
        u1.getUseremails()
                .add(new Useremail(u1,
                        "admin@email.local"));

        u1.getUseremails().get(0).setUseremailid(11);

        u1.getUseremails()
                .add(new Useremail(u1,
                        "admin@mymail.local"));

        u1.getUseremails().get(1).setUseremailid(12);

//        userService.save(u1);
        userList.add(u1);

        // data, user
        User u2 = new User("cinnamon test",
                "1234567",
                "cinnamon@lambdaschool.local");
        u2.setUserid(2);

        u2.getRoles()
                .add(new UserRoles(u2,
                        r2));
        u2.getRoles()
                .add(new UserRoles(u2,
                        r3));
        u2.getUseremails()
                .add(new Useremail(u2,
                        "cinnamon@mymail.local"));
        u2.getUseremails().get(0).setUseremailid(12);

        u2.getUseremails()
                .add(new Useremail(u2,
                        "hops@mymail.local"));

        u2.getUseremails().get(1).setUseremailid(13);

        u2.getUseremails()
                .add(new Useremail(u2,
                        "bunny@email.local"));

        u2.getUseremails().get(2).setUseremailid(14);

//        userService.save(u2);
        userList.add(u2);

        // user
        User u3 = new User("barnbarn test",
                "ILuvM4th!",
                "barnbarn@lambdaschool.local");

        u3.setUserid(3);

        u3.getRoles()
                .add(new UserRoles(u3,
                        r2));
        u3.getUseremails()
                .add(new Useremail(u3,
                        "barnbarn@email.local"));

        u3.getUseremails().get(0).setUseremailid(15);

//        userService.save(u3);
        userList.add(u3);

        User u4 = new User("puttat test",
                "password",
                "puttat@school.lambda");

        u4.setUserid(4);

        u4.getRoles()
                .add(new UserRoles(u4,
                        r2));
//        userService.save(u4);
        userList.add(u4);

        User u5 = new User("misskitty test",
                "password",
                "misskitty@school.lambda");

        u5.setUserid(5);

        u5.getRoles()
                .add(new UserRoles(u5,
                        r2));
//        userService.save(u5);
        userList.add(u5);

        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void listAllUsers() throws Exception
    {
        String apiURL = "/users/users";
        Mockito.when(userService.findAll())
                .thenReturn(userList);

        RequestBuilder rb = MockMvcRequestBuilders.get(apiURL)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb)
                .andReturn();
        String tr = r.getResponse()
                .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(userList);

        System.out.println(er);
        assertEquals(er, tr);
    }

    @Test
    public void getUserById() throws Exception
    {
        String apiURL = "/users/user/1";
        Mockito.when(userService.findUserById(1))
                .thenReturn(userList.get(0));

        RequestBuilder rb = MockMvcRequestBuilders.get(apiURL)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb)
                .andReturn();
        String tr = r.getResponse()
                .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(userList.get(0));

        System.out.println(tr);
        assertEquals(er, tr);
    }

    @Test
    public void getUserByIdNotFound() throws Exception
    {
        String apiURL = "/users/user/100";
        Mockito.when(userService.findUserById(1))
                .thenReturn(null);

        RequestBuilder rb = MockMvcRequestBuilders.get(apiURL)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb)
                .andReturn();
        String tr = r.getResponse()
                .getContentAsString();

        String er = "";

        System.out.println(tr);
        assertEquals(er, tr);
    }

    @Test
    public void getUserByName() throws Exception
    {
        String apiUrl = "/users/user/name/barnbarn test";

        Mockito.when(userService.findByName("barnbarn test"))
                .thenReturn(userList.get(0));

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb)
                .andReturn(); // this could throw an exception
        String tr = r.getResponse()
                .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(userList.get(0));

        System.out.println("Expect: " + er);
        System.out.println("Actual: " + tr);

        Assert.assertEquals("Rest API Returns List",
                er,
                tr);
    }

    @Test
    public void getUserLikeName() throws Exception
    {
        String apiUrl = "/users/user/name/like/barn";

        Mockito.when(userService.findByNameContaining("barn"))
                .thenReturn(userList);

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
                .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb)
                .andReturn(); // this could throw an exception
        String tr = r.getResponse()
                .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(userList);

        System.out.println("Expect: " + er);
        System.out.println("Actual: " + tr);

        Assert.assertEquals("Rest API Returns List",
                er,
                tr);
    }

    @Test
    public void addNewUser() throws Exception
    {
        String apiUrl = "/users/user";

//        User u6 = new User("mojo",
//                "cofee123",
//                "mojo@school.lambda");
//
//        Role r2 = new Role("user");
//        r2.setRoleid(2);
//
//        u6.getRoles()
//                .add(new UserRoles(u6,
//                        r2));
//        userList.add(u6);

//        ObjectMapper mapper = new ObjectMapper();
//        String userString = mapper.writeValueAsString(u6);

        Mockito.when(userService.save(any(User.class)))
                .thenReturn(userList.get(0));

        String userString = "{\"username\": \"mojo\", \"password\": \"123456\", \"primaryemail\": \"mojo@mojo.com\"}";

        RequestBuilder rb = MockMvcRequestBuilders.post(apiUrl)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userString);

        mockMvc.perform(rb)
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateFullUser() {
    }

    @Test
    public void updateUser() {
    }

    @Test
    public void deleteUserById() {
    }

    @Test
    public void getCurrentUserInfo() {
    }
}