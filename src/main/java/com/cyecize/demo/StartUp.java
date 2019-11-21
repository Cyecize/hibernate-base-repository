package com.cyecize.demo;

import com.cyecize.demo.entities.User;
import com.cyecize.demo.repositories.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Scanner;

public class StartUp {
    public static void main(String[] args) {
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("baseRepositoryDemo");
        final EntityManager entityManager = emf.createEntityManager();

        final UserRepository userRepository = new UserRepository(entityManager);

        final Scanner scanner = new Scanner(System.in);

        printCommandsInfo();
        while (true) {
            System.out.println("write your command");
            final String line = scanner.nextLine();
            if (line == null || line.isEmpty() || line.equalsIgnoreCase("break")) break;

            final String[] tokens = line.split("\\s+");

            try {
                switch (tokens[0].toLowerCase()) {
                    case "create":
                        final User user = new User();
                        user.setUsername(tokens[1]);
                        user.setHometown(tokens[2]);

                        userRepository.persist(user);
                        System.out.println(user);
                        break;
                        
                    case "all":
                        System.out.println(userRepository.findAll());
                        break;

                    case "id":
                        System.out.println(userRepository.find(Long.parseLong(tokens[1])));
                        break;

                    case "username":
                        System.out.println(userRepository.findByUsername(tokens[1]));
                        break;

                    case "hometown":
                        System.out.println(userRepository.findByHometown(tokens[1]));
                        break;

                    case "hometownnumber":
                        System.out.println(userRepository.findNumberOfUsersForHometown(tokens[1]));
                        break;

                    case "newhometown":
                        final User userToEdit = userRepository.findByUsername(tokens[1]);
                        if (userToEdit == null) System.out.println("Invalid User");
                        else {
                            userToEdit.setHometown(tokens[2]);
                            userRepository.merge(userToEdit);
                            System.out.println(userToEdit);
                        }
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        System.out.println("closing app");
        entityManager.close();
        emf.close();
    }

    private static void printCommandsInfo() {
        System.out.println("Available Commands: ");
        System.out.println("break - close the app");
        System.out.println("create {username} {hometown} - create new user");
        System.out.println("all - find all users");
        System.out.println("id {id} - find by id");
        System.out.println("username {username} - find by username");
        System.out.println("hometown {hometown} - find by hometown");
        System.out.println("hometownNumber {hometown} - find number of users for hometown");
        System.out.println("newHometown {username} {new hometown}");
    }
}
