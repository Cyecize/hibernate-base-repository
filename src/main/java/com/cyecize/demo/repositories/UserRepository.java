package com.cyecize.demo.repositories;

import com.cyecize.baserepository.BaseRepository;
import com.cyecize.demo.entities.User;

import javax.persistence.EntityManager;
import java.util.List;


public class UserRepository extends BaseRepository<User, Long> {

    private static final String USERNAME_FIELD_NAME = "username";

    private static final String HOMETOWN_FIELD_NAME = "hometown";

    public UserRepository(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * This method is using the queryBuilderSingle approach, which executes a query and gets only the first result.
     *
     * @param username - username
     */
    public User findByUsername(String username) {
        return super.queryBuilderSingle((userCriteriaQuery, userRoot) -> userCriteriaQuery.where(
                super.criteriaBuilder.equal(userRoot.get(USERNAME_FIELD_NAME), username)
        ));
    }

    /**
     * This method is using the queryBuilderList approach, which executes a query and gets all records.
     *
     * @param hometown - hometown
     */
    public List<User> findByHometown(String hometown) {
        return super.queryBuilderList((userCriteriaQuery, userRoot) -> userCriteriaQuery.where(
                super.criteriaBuilder.equal(userRoot.get(HOMETOWN_FIELD_NAME), hometown)
        ));
    }

    /**
     * Select the number of users for a given hometown.
     * <p>
     * This method is using the query approach and returns just one item (the count) instead a list of users.
     *
     * @param hometown - hometown
     */
    public Long findNumberOfUsersForHometown(String hometown) {
        return super.execute(ar -> ar.set(
                super.entityManager.createQuery("SELECT COUNT (u) FROM User u WHERE u.hometown = :hometown", Long.class)
                        .setParameter("hometown", hometown)
                        .getSingleResult()
        ), Long.class).get();
    }
}
