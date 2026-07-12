import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table (name = "myusers")
public class User {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    @Column (name = "name")
    private String name;

    @Column (name = "email")
    private String email;

    @Column (name = "age")
    private int age;

    @Column (name = "created_at")
    private LocalDateTime createdAt;

    public User() {}

//    public void setId(int id) {
//        this.id = id;
//    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("/%d, %s, %s, %d, ", id, name, email, age) + createdAt + "/";
    }
}