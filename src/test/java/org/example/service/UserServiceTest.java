package org.example.service;

import org.example.dao.UserDao;
import org.example.entity.User;
import org.example.exceptions.EmailAlreadyExistsException;
import org.example.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @Test
    void testCreateUser_Success() {
        User user = new User("Иванов Иван", "i.ivan@mail.com", 25);
        Mockito.when(userDao.findByEmail(user.getEmail())).thenReturn(null);
        userService.createUser(user);
        verify(userDao).create(any(User.class));
    }

    @Test
    void testCreateUser_EmailAlreadyExist() {
        User user = new User("Иванов Иван", "i.ivan@mail.com", 25);
        User existentUser = new User("Иванов Иван", "i.ivan@mail.com", 25);
        Mockito.when(userDao.findByEmail(user.getEmail())).thenReturn(existentUser);
        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(user));
        verify(userDao, never()).create(any(User.class));
    }

    @Test
    void testGetUserById_Success() {
        User user = new User("Иванов Иван", "i.ivan@mail.com", 25);
        Mockito.when(userDao.findById(anyInt())).thenReturn(user);
        User findingUser = userService.getUserById(user.getId());
        assertEquals(findingUser, user);
        verify(userDao).findById(anyInt());
    }

    @Test
    void testGetUserById_NonExistentId() {
        Mockito.when(userDao.findById(-1)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(-1));
        verify(userDao).findById(-1);
    }

    @Test
    void getAllUsers() {
        User user1 = new User("Иванов Иван", "i.ivan@mail.com", 25);
        User user2 = new User("Петров Петр", "p.petrov@mail.com", 30);
        List<User> list = new ArrayList<>();
        list.add(user1);
        list.add(user2);
        Mockito.when(userDao.findAll()).thenReturn(list);
        List<User> allUsers = userService.getAllUsers();
        assertEquals(list, allUsers);
        verify(userDao).findAll();
    }

    @Test
    void getAllUsers_EmptyTable() {
        List<User> list = List.of();
        Mockito.when(userDao.findAll()).thenReturn(list);
        List<User> allUsers = userService.getAllUsers();
        assertEquals(list, allUsers);
        assertTrue(allUsers.isEmpty());
        verify(userDao).findAll();
    }

    @Test
    void getUsersByAge() {
        User user1 = new User("Иванов Иван", "i.ivan@mail.com", 25);
        User user2 = new User("Денисов Денис", "d.denis@mail.com", 25);
        List<User> years25 = List.of(user1, user2);
        Mockito.when(userDao.findByAge(25)).thenReturn(years25);
        List<User> findingUsers = userService.getUsersByAge(25);
        assertEquals(years25, findingUsers);
        verify(userDao).findByAge(25);
    }

    @Test
    void getUsersByAge_NoSameUsers() {
        List<User> list = List.of();
        Mockito.when(userDao.findByAge(999)).thenReturn(list);
        List<User> findingUsers = userService.getUsersByAge(999);
        assertTrue(findingUsers.isEmpty());
        assertEquals(findingUsers, list);
        verify(userDao).findByAge(999);
    }

    @Test
    void testUpdateUser_Success() {
        User user = new User("Иванов Иван", "i.ivan@mail.com", 25);
        when(userDao.findById(anyInt())).thenReturn(user);
        when(userDao.findByEmail("p.petrov@mail.com")).thenReturn(null);
        userService.updateUser(1, "Петров Петр", "p.petrov@mail.com", 30);
        verify(userDao).update(anyInt(), eq("Петров Петр"), eq("p.petrov@mail.com"), eq(30));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        int id = -1;
        when(userDao.findById(id)).thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(id, "Петр", "new@mail.com", 30));
        verify(userDao, never()).update(anyInt(), anyString(), anyString(), anyInt());
    }

    @Test
    void testUpdateUser_EmailAlreadyExists() {
        User user = new User("Иванов Иван", "i.ivan@mail.com", 25);
        User anotherUser = new User("Петров Петр", "p.petrov@mail.com", 30);
        when(userDao.findById(anyInt())).thenReturn(user);
        when(userDao.findByEmail(anyString())).thenReturn(anotherUser);
        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUser(user.getId(), "Петр Петров", "petr.p@mail.com", 30));
        verify(userDao, never()).update(anyInt(), anyString(), anyString(), anyInt());
    }

    @Test
    void testRemoveUser_Success() {
        User user = new User("Иванов Иван", "i.ivan@mail.com", 25);
        when(userDao.findById(anyInt())).thenReturn(user);
        userService.removeUser(user.getId());
        verify(userDao).remove(anyInt());
    }

    @Test
    void testRemoveUser_UserNotFound() {
        int id = -999;
        when(userDao.findById(id)).thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> userService.removeUser(id));
        verify(userDao, never()).remove(anyInt());
    }
}