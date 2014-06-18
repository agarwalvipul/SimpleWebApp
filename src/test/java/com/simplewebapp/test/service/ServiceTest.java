package com.simplewebapp.test.service;

import com.simplewebapp.config.AppConfig;
import com.simplewebapp.model.User;
import com.simplewebapp.service.UserService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class ServiceTest {

    Logger logger = LoggerFactory.getLogger(ServiceTest.class);

    @Autowired
    UserService userService;

    private static User user;

    public static User getUser() {
        if (user == null) {
            user = new User();
            user.setName("UnitTest_username");
            user.setEmail("UnitTest_email");
            user.setPasswordHash("HASH");
            user.setEnabled(false);
        }
        return user;
    }

    @Ignore
    @Test
    public void testCRUD() {
//        Create User
        userService.addUser(getUser());

//        Get User by Email
        User updatedUser = userService.getUser("UnitTest_email");
        Assert.assertEquals(getUser(), updatedUser);

//        Get User by Id
        updatedUser = userService.getUser(updatedUser.getId());
        Assert.assertEquals(getUser(), updatedUser);

//        Update User
        String newName = "UnitTest_NewName";
        updatedUser.setName(newName);
        userService.changeUser(updatedUser);
        updatedUser = userService.getUser(updatedUser.getId());

        Assert.assertEquals(getUser().getId(), updatedUser.getId());
        Assert.assertEquals(newName, updatedUser.getName());
        Assert.assertEquals(getUser().getPasswordHash(), updatedUser.getPasswordHash());
        Assert.assertEquals(getUser().getEmail(), updatedUser.getEmail());
        Assert.assertEquals(getUser().isEnabled(), updatedUser.isEnabled());
//        todo: Add test for Unique constraint violation
//        Add Existing User
//        userDAO.addUser(getUser());

//        Delete User
        userService.removeUser(getUser());
        updatedUser = userService.getUser(getUser().getId());
        Assert.assertNull(updatedUser);
    }

    @Ignore
    @Test
    public void testConnectionPoolUser() {
        for (int i = 0; i < 100; i++) {
        User user = getUser();
        user.setEmail("UnitTest_EmailId_"+String.valueOf(i));
//        logger.info(user.toString());
            userService.addUser(user);
        }
    }

}
