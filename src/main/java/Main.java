import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.List;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static int correctReadInt(Scanner sc, String str) {
        while (true) {
            IO.println(str);
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                log.error("Incorrect enter {}", e.getMessage());
                IO.println("Incorrect input! Enter correct number!");
            }
        }
    }
    public static void main (String[] args) {

        Configuration conf = new Configuration().configure();
        SessionFactory sf = conf.buildSessionFactory();
        UserDao userDao = new UserDao(sf);
        UserService userService = new UserService(userDao);

        try (Scanner sc = new Scanner(System.in)) {
            boolean work = true;
            List<User> users = null;
            while (work) {
                IO.println("======================================" +
                        "\nChose operation:" +
                        "\n1. Find all Users" +
                        "\n2. Find User by ID" +
                        "\n3. Find Users by age" +
                        "\n4. Create User" +
                        "\n5. Update User" +
                        "\n6. Remove User" +
                        "\n7. Exit");
                int choice = correctReadInt(sc, "Your choice: ");

                if (choice > 7 || choice < 1) {
                    throw new IllegalArgumentException("Your choice is illegal! Correct choice is 1-7!");
                }

                else if (choice==1) {
                    IO.println("Finding all Users...");
                    users = userService.getAllUsers();
                    if (users.isEmpty()) {
                        IO.println("No Users in a Table");
                    } else {
                        IO.println(users);
                    }
                }

                else if (choice==2) {
                    IO.println("Finding User by ID...");
                    int findingId = correctReadInt(sc, "Enter ID: ");
                    User user = userService.getUserById(findingId);
                    if (user==null) {
                        IO.println("User with ID " + findingId + " not found!");
                    } else {
                        IO.println(user);
                    }
                }

                else if (choice==3) {
                    IO.print("Finding User by age...");
                    int findingAge = correctReadInt(sc, "Enter age: ");
                    users = userService.getUsersByAge(findingAge);
                    if (users.isEmpty()) {
                        IO.println("No Users with age " + findingAge + " in Table");
                    } else {
                        IO.println(users);
                    }
                }

                else if (choice==4) {
                    IO.println("Creating User...");
                    IO.print("name: ");
                    String name = sc.nextLine();
                    IO.print("email: ");
                    String email = sc.nextLine();
                    int age = correctReadInt(sc, "age: ");
                    userService.createUser(new User(name, email, age));
                }

                else if (choice==5) {
                    IO.println("Updating User...");
                    try {
                        int id = correctReadInt(sc, "Enter User's ID: ");
                        IO.print("new name: ");
                        String newName = sc.nextLine();
                        IO.print("new email: ");
                        String newEmail = sc.nextLine();
                        int newAge = correctReadInt(sc, "new age: ");
                        userService.updateUser(id, newName, newEmail, newAge);
                        IO.println("User has been updated!");
                    } catch (RuntimeException e) {
                        log.error("Updating User failed: {}", e.getMessage());
                        IO.println("Error " + e.getMessage());
                    }

                }

                else if (choice==6) {
                    IO.println("Removing User...");
                    try {
                        int id = correctReadInt(sc, "Enter User's ID: ");
                        userService.removeUser(id);
                        IO.println("User has been removed!");
                    } catch (RuntimeException e) {
                        log.error("Removing User failed: {}", e.getMessage());
                        IO.println("Error " + e.getMessage());
                    }

                }

                else if (choice==7) {
                    IO.println("Exit...");
                    work = false;
                }
            }
        }
    }
}