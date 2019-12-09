package com.cyecize.demo;

import com.cyecize.baserepository.pagination.Pageable;
import com.cyecize.demo.entities.User;
import com.cyecize.demo.repositories.UserRepository;
import com.google.gson.Gson;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Scanner;

public class StartUp {

    private static final Gson gson = new Gson();

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
                        print(user);
                        break;

                    case "all":
                        print(userRepository.findAll());
                        break;

                    case "allpage":
                        print(userRepository.findAll(Pageable.of(
                                Integer.parseInt(tokens[1]),
                                Integer.parseInt(tokens[2])
                        )));
                        break;

                    case "id":
                        print(userRepository.find(Long.parseLong(tokens[1])));
                        break;

                    case "username":
                        print(userRepository.findByUsername(tokens[1]));
                        break;

                    case "hometown":
                        print(userRepository.findByHometown(tokens[1]));
                        break;

                    case "hometownpage":
                        print(userRepository.findByHometown(tokens[1], Pageable.of(
                                Integer.parseInt(tokens[2]),
                                Integer.parseInt(tokens[3])
                        )));
                        break;

                    case "hometownnumber":
                        print(userRepository.findNumberOfUsersForHometown(tokens[1]));
                        break;

                    case "newhometown":
                        final User userToEdit = userRepository.findByUsername(tokens[1]);
                        if (userToEdit == null) System.out.println("Invalid User");
                        else {
                            userToEdit.setHometown(tokens[2]);
                            userRepository.merge(userToEdit);
                            print(userToEdit);
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

    private static void print(Object o) {
        System.out.println(gson.toJson(o));
    }

    private static void printCommandsInfo() {
        System.out.println("Available Commands: ");
        System.out.println("break - close the app");
        System.out.println("create {username} {hometown} - create new user");
        System.out.println("all - find all users");
        System.out.println("allPage {page} {size} - find all users paginated");
        System.out.println("id {id} - find by id");
        System.out.println("username {username} - find by username");
        System.out.println("hometown {hometown} - find by hometown");
        System.out.println("hometownPage {hometown} {page} {size} - find by hometown paginated");
        System.out.println("hometownNumber {hometown} - find number of users for hometown");
        System.out.println("newHometown {username} {new hometown}");
    }
}
