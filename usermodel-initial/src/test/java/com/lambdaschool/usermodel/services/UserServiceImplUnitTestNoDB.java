package com.lambdaschool.usermodel.services;

import com.lambdaschool.usermodel.UserModelApplicationTesting;
import com.lambdaschool.usermodel.exceptions.ResourceNotFoundException;
import com.lambdaschool.usermodel.models.Role;
import com.lambdaschool.usermodel.models.User;
import com.lambdaschool.usermodel.models.UserRoles;
import com.lambdaschool.usermodel.models.Useremail;
import com.lambdaschool.usermodel.repository.RoleRepository;
import com.lambdaschool.usermodel.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UserModelApplicationTesting.class,
    properties = {"command.line.runner.enabled=false"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceImplUnitTestNoDB {

    // stubs -> fake methods
    // mocks -> fake data
    // Java - call everything mocks

    // findbyid
    // findall
    // Name by like
    // POST
    // OUT
    // DELETE - the big 6 to test everytime

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userrepos;

    @MockBean
    private RoleService roleService;

    @MockBean
    private RoleRepository rolerepos;

    @MockBean
    private HelperFunctions helperFunctions;

    private List<User> userList;

    @Before
    public void setUp() throws Exception {

        userList = new ArrayList<>();

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
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void c_findUserById()
    {
        Mockito.when(userrepos.findById(3L))
                .thenReturn(Optional.of(userList.get(2)));

                assertEquals("barnbarn test", userService.findUserById(3).getUsername());
    }

    @Test
    public void c_notFindUserById()
    {
        Mockito.when(userrepos.findById(300L))
                .thenThrow(ResourceNotFoundException.class);

        assertEquals("barnbarn test", userService.findUserById(300).getUsername());
    }

    @Test
    public void b_findByNameContaining()
    {
        Mockito.when(userrepos.findByUsernameContainingIgnoreCase("a"))
                .thenReturn(userList);

        assertEquals(5, userService.findByNameContaining("a").size());
    }

    @Test
    public void a_findAll()
    {
        Mockito.when(userrepos.findAll())
                .thenReturn(userList);

        assertEquals(5, userService.findAll().size());
    }

    @Test
    public void e_delete() {

        Mockito.when(userrepos.findById(3L))
                .thenReturn(Optional.of(userList.get(0)));

        Mockito.doNothing()
                .when(userrepos)
                .deleteById(3L);

        userService.delete(3);
        assertEquals(5, userList.size());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void e_deleteFailed() {

        Mockito.when(userrepos.findById(333L))
                .thenReturn(Optional.empty());

        Mockito.doNothing()
                .when(userrepos)
                .deleteById(333L);

        userService.delete(333);
        assertEquals(5, userList.size());
    }

    @Test
    public void findByName()
    {
        Mockito.when(userrepos.findByUsernameContainingIgnoreCase("barnbarn test"))
                .thenReturn(userList);

        assertEquals(5, userService.findByNameContaining("barnbarn test")
                .size());
    }

    @Test
    public void d_save()
    {
        String username = "test test test";
        User u6 = new User(username,
                "password",
                "test@school.lambda");

//        u6.setUserid(6);

        Role r2 = new Role("user");
        r2.setRoleid(2);

        u6.getRoles()
                .add(new UserRoles(u6,
                        r2));

        userList.add(u6);

        Mockito.when(userrepos.save(any(User.class)))
                .thenReturn(u6);

        User addUser = userService.save(u6);
        assertNotNull(addUser);
        assertEquals(username, addUser.getUsername());
    }

    @Test
    public void update() {
    }

    @Test
    public void deleteAll() {
    }
}