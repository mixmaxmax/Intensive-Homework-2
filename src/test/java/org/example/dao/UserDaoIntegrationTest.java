package org.example.dao;

import org.example.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserDaoIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private SessionFactory sessionFactory;
    private UserDao userDao;

    @BeforeAll
    void setUp() {
        Configuration configuration = new Configuration();
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", postgreSQLContainer.getJdbcUrl());
        configuration.setProperty("hibernate.connection.username", postgreSQLContainer.getUsername());
        configuration.setProperty("hibernate.connection.password", postgreSQLContainer.getPassword());
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.format_sql", "true");
        configuration.setProperty("hibernate.current_session_context_class", "thread");
        configuration.addAnnotatedClass(User.class);

        sessionFactory = configuration.buildSessionFactory();
        userDao = new UserDao(sessionFactory);
    }

    @AfterAll
    void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void clearTable() {
        try (var session = sessionFactory.openSession()) {
            var tx = session.beginTransaction();
            session.createNativeQuery("TRUNCATE myusers RESTART IDENTITY").executeUpdate();
            tx.commit();
        }
    }

    @Test
    void testCreateUser() {
        User user = new User("Иванов Иван", "i.ivan@mail.com", 25);
        userDao.create(user);
        assertTrue(user.getId() > 0);
    }

    @Test
    void testFindByID() {
        User user = new User("Иванов Иван", "i.ivan@mail.com", 25);
        userDao.create(user);
        User createdUser = userDao.findById(user.getId());
        assertEquals(createdUser.getId(), user.getId());
    }

    @Test
    void testNonExistentId() {
        int notExistentId = -999;
        User user = userDao.findById(notExistentId);
        assertNull(user);
    }

    @Test
    void testFindByExistingEmail() {
        String email = "i.ivan@mail.com";
        User user = new User("Иванов Иван", email, 25);
        userDao.create(user);
        User findingUser = userDao.findByEmail(email);
        assertEquals(user, findingUser);
    }

    @Test
    void testFindByNonExistentEmail() {
        String nonExistentEmail = "!@#$%^&";
        User user = userDao.findByEmail(nonExistentEmail);
        assertNull(user);
    }

    @Test
    void testFindAllUsers() {
        User user1 = new User("Иванов Иван", "i.ivan@mail.com", 25);
        User user2 = new User("Денисов Денис", "d.den@mail.com", 30);
        userDao.create(user1);
        userDao.create(user2);
        List<User> createdUsers = userDao.findAll();
        assertEquals(2, createdUsers.size());

        User[] createdUsersArr = createdUsers.toArray(new User[0]);
        assertEquals(user1, createdUsersArr[0]);
        assertEquals(user2, createdUsersArr[1]);
    }

    @Test
    void testFinaAllInEmtyTable() {
        List<User> list = userDao.findAll();
        assertTrue(list.isEmpty());
    }

    @Test
    void testFindByAge() {
        User user1 = new User("Иванов Иван", "i.ivan@mail.com", 25);
        User user2 = new User("Денисов Денис", "d.den@mail.com", 30);
        userDao.create(user1);
        userDao.create(user2);
        List<User> findingUser = userDao.findByAge(25);
        assertEquals(user1, findingUser.getFirst());
    }

    @Test
    void testFindByNonExistentAge() {
        List<User> list = userDao.findByAge(10000);
        assertTrue(list.isEmpty());
    }

    @Test
    void testUpdateUser() {
        User user = new User("Иванов Иван", "i.ivan@mail.com", 25);
        userDao.create(user);
        int id = user.getId();

        userDao.update(id, "Петров Петр", "p.petrov@mail.com", 30);

        User updated = userDao.findById(id);
        assertNotNull(updated);
        assertEquals("Петров Петр", updated.getName());
        assertEquals("p.petrov@mail.com", updated.getEmail());
        assertEquals(30, updated.getAge());
    }

    @Test
    void testUpdateNonExistentUser() {
        assertThrows(RuntimeException.class, () -> userDao.update(-999, "x", "x@x.com", 0));
    }

    @Test
    void testRemoveUser() {
        User user = new User("Иванов Иван", "i.ivan@mail.com", 25);
        userDao.create(user);
        int id = user.getId();
        userDao.remove(id);
        assertNull(userDao.findById(id));
    }

    @Test
    void testRemoveNonExistentUser() {
        assertThrows(RuntimeException.class, () -> userDao.remove(-999));
    }
}