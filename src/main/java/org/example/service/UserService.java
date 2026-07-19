package org.example.service;

import org.example.dao.UserDao;
import org.example.entity.User;
import org.example.exceptions.EmailAlreadyExistsException;
import org.example.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserDao userDao;

    public UserService (UserDao userDao) {
        this.userDao = userDao;
    }

    public void createUser (User user) {
            if (userDao.findByEmail(user.getEmail()) == null) {
                userDao.create(user);
            } else {
                log.error("Failed to create org.example.entity.User. Email already exists.");
                throw new EmailAlreadyExistsException("This email already exists! org.example.entity.User has not been created!");
            }
    }

    public User getUserById(int id) {
        User user = userDao.findById(id);
        if (user != null) {
            return user;
        } else {
            log.error("Failed to find org.example.entity.User by id: {}. org.example.entity.User not found!", id);
            throw new UserNotFoundException("org.example.entity.User with id " + id + " not found");
        }
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public List<User> getUsersByAge(int age) {
        return userDao.findByAge(age);
    }

    public void updateUser(int id, String newName, String newEmail, int newAge) {
        User user = userDao.findById(id);
        if (user == null) {
            log.error("Update error. org.example.entity.User with id: {} not found.", id);
            throw new UserNotFoundException("org.example.entity.User not found. org.example.entity.User has not been updated!");
        }

        if (!user.getEmail().equals(newEmail)) {
            User existing = userDao.findByEmail(newEmail);
            if (existing != null) {
                log.error("Update error. Email {} already exists.", newEmail);
                throw new EmailAlreadyExistsException("Email " + newEmail + " already exists!");
            }
        }

        userDao.update(id, newName, newEmail, newAge);
    }

    public void removeUser(int id) {
        User user = userDao.findById(id);
        if (user != null) {
            userDao.remove(id);
        } else {
            log.error("Removing faild. org.example.entity.User with id: {} not found.", id);
            throw new UserNotFoundException("org.example.entity.User not found. org.example.entity.User has not been removed!");
        }
    }
}