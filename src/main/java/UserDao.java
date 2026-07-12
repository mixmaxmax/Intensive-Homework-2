import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDao {
    private static final Logger log = LoggerFactory.getLogger(UserDao.class);
    private final SessionFactory sf;
    public UserDao (SessionFactory sf) {
        this.sf = sf;
    }

    /**Сохранение экземпляра сущности в таблицу*/
    public void create(User user) {
        try (Session session = sf.openSession()) {
            Transaction tr = session.beginTransaction();
            try {
                session.persist(user);
                tr.commit();
            } catch (HibernateException e) {
                log.error("Failed to save User: {}", e.getMessage());
                throw new RuntimeException("User has not bees saved! " + e.getMessage());
            }
        }
    }

    /**Поиск по pk*/
    public User findById(int id) {
        try (Session session = sf.openSession()) {
            Transaction tr = session.beginTransaction();
            try {
                User user = session.find(User.class, id);
                tr.commit();
                return user;
            } catch (Exception e) {
                log.error("Failed to find User by id: {}", e.getMessage());
                throw new RuntimeException("User with id [" + id + "] not found" + e.getMessage());
            }
        }
    }

    /**Поиск по возрасту*/
    public List<User> findByAge(int age) {
        try (Session session = sf.openSession()) {
            Transaction tr = session.beginTransaction();
            try {
                CriteriaBuilder cb = session.getCriteriaBuilder();
                CriteriaQuery<User> criteriaQuery = cb.createQuery(User.class);
                Root<User> root = criteriaQuery.from(User.class);
                criteriaQuery.select(root).where(cb.equal(root.get("age"), age));
                List<User> users = session.createQuery(criteriaQuery).getResultList();
                tr.commit();
                return users;
            } catch (Exception e) {
                log.error("Failed to find User(s) by age: {}", e.getMessage());
                throw new RuntimeException("User(s) with age ["+ age +"] not found! " + e.getMessage());
            }
        }
    }

    /**Возвращает все записи из таблицы*/
    public List<User> findAll() {
        try (Session session = sf.openSession()) {
            Transaction tr = session.beginTransaction();
            try {
                String hql = "FROM User ORDER BY id";
                Query query = session.createQuery(hql);
                List<User> users = query.getResultList();
                tr.commit();
                return users;
            } catch (Exception e) {
                log.error("Failed to find Users in Table: {}", e.getMessage());
                throw new RuntimeException("Users nor found! " + e.getMessage());
            }
        }
    }

    /**Обновляет запись (Обновляет все поля кроме pk и createdAt)*/
    public void update(int id, String newName, String newEmail, int newAge) {
        try (Session session = sf.openSession()) {
            Transaction tr = session.beginTransaction();
            try {
                User user = session.find(User.class, id);
                if (user == null) {
                    throw new RuntimeException("User with id " + id + " not found!");
                }
                user.setName(newName);
                user.setEmail(newEmail);
                user.setAge(newAge);
                tr.commit();
            } catch (Exception e) {
                tr.rollback(); //отмена транзакции
                log.error("Failed to update User: {}", e.getMessage());
                throw new RuntimeException("User has not been updated! " + e.getMessage());
            }
        }
    }

    /**Удаляет запись из таблицы*/
    public void remove(int id) {
        try (Session session = sf.openSession()) {
            Transaction tr = session.beginTransaction();
            try {
                User user = session.find(User.class, id);
                if (user == null) {
                    throw new RuntimeException("User with id " + id + " not found");
                }
                session.remove(user);
                tr.commit();
            } catch (Exception e) {
                log.error("Failed to remove User", e.getMessage());
                throw new RuntimeException("User has not been deleted! " + e.getMessage());
            }
        }
    }
}