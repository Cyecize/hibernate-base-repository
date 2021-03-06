package com.cyecize.baserepository;

import com.cyecize.baserepository.pagination.Page;
import com.cyecize.baserepository.pagination.PageImpl;
import com.cyecize.baserepository.pagination.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Base repository implementation.
 * This class is as generic as possible. The only dependency that it relies on is {@link EntityManager}
 * If you are using spring or other platform that supports Dependency Injection you can inject it that way or
 * manually.
 *
 * @param <E>  - the type of the entity.
 * @param <ID> - the type of the primary key.
 */
public abstract class BaseRepository<E, ID> {

    public static final class ActionResult<T> {
        private T result;

        public T get() {
            return this.result;
        }

        public void set(T result) {
            this.result = result;
        }
    }

    protected final EntityManager entityManager;

    protected final Class<E> persistentClass;

    protected final Class<ID> primaryKeyType;

    protected CriteriaBuilder criteriaBuilder;

    @SuppressWarnings("unchecked")
    public BaseRepository(EntityManager entityManager) {
        this.entityManager = entityManager;

        final ParameterizedType parameterizedType = this.getParameterizedType();
        this.persistentClass = (Class<E>) parameterizedType.getActualTypeArguments()[0];
        this.primaryKeyType = (Class<ID>) parameterizedType.getActualTypeArguments()[1];
    }

    public void persist(E entity) {
        this.executeNonResult((() -> this.entityManager.persist(entity)));
    }

    public void merge(E entity) {
        this.executeNonResult(() -> this.entityManager.merge(entity));
    }

    public void remove(E entity) {
        this.executeNonResult(() -> this.entityManager.remove(entity));
    }

    public Long count() {
        final String query = String.format("SELECT count (t) FROM %s t", this.persistentClass.getSimpleName());

        return this.execute(
                ar -> ar.set(this.entityManager.createQuery(query, Long.class).getSingleResult()),
                Long.class
        ).get();
    }

    public E find(ID id) {
        return this.execute(ar -> ar.set(this.entityManager.find(this.persistentClass, id))).get();
    }

    public List<E> findAll() {
        //Empty method means no conditions, therefore select all
        return this.queryBuilderList(((eCriteriaQuery, eRoot) -> {
        }));
    }

    public Page<E> findAll(Pageable pageable) {
        return this.queryBuilderList((eCriteriaQuery, eRoot) -> {
        }, pageable);
    }

    protected synchronized <T> ActionResult<T> execute(Consumer<ActionResult<T>> invoker, Class<? extends T> returnType) {
        this.criteriaBuilder = this.entityManager.getCriteriaBuilder();

        final ActionResult<T> actionResult = new ActionResult<>();
        final EntityTransaction transaction = this.entityManager.getTransaction();

        transaction.begin();
        try {
            invoker.accept(actionResult);
            transaction.commit();
        } catch (Exception ex) {
            transaction.rollback();
            throw new RuntimeException(ex);
        }

        return actionResult;
    }

    protected ActionResult<E> execute(Consumer<ActionResult<E>> invoker) {
        return this.execute(invoker, this.persistentClass);
    }

    private void executeNonResult(Runnable invoker) {
        this.execute(actionResult -> invoker.run());
    }

    @SuppressWarnings("unchecked")
    protected <T> T queryBuilderSingle(BiConsumer<CriteriaQuery<E>, Root<E>> invoker, Class<T> returnType) {
        return this.execute(ar -> {
            final CriteriaQuery<E> criteria = this.criteriaBuilder.createQuery(this.persistentClass);
            final Root<E> root = criteria.from(this.persistentClass);
            criteria.select(root);

            invoker.accept(criteria, root);

            ar.set((T) this.entityManager.createQuery(criteria).getResultStream().findFirst().orElse(null));
        }, returnType).get();
    }

    protected E queryBuilderSingle(BiConsumer<CriteriaQuery<E>, Root<E>> invoker) {
        return this.queryBuilderSingle(invoker, this.persistentClass);
    }

    @SuppressWarnings("unchecked")
    protected Page<E> queryBuilderList(BiConsumer<CriteriaQuery<E>, Root<E>> invoker, Pageable pageable) {
        return this.execute(ar -> {
            final CriteriaQuery<E> criteria = this.criteriaBuilder.createQuery(this.persistentClass);
            final Root<E> root = criteria.from(this.persistentClass);
            criteria.select(root);

            invoker.accept(criteria, root);

            final Long totalCount = this.getTotalCount(root, criteria);
            final List<E> items = this.addPagination(
                    pageable,
                    this.entityManager.createQuery(criteria)
            ).getResultList();

            ar.set(new PageImpl(
                    pageable,
                    items,
                    totalCount
            ));
        }, Page.class).get();
    }

    protected List<E> queryBuilderList(BiConsumer<CriteriaQuery<E>, Root<E>> invoker) {
        return this.queryBuilderList(invoker, Pageable.of(1, Integer.MAX_VALUE)).getItems();
    }

    private ParameterizedType getParameterizedType() {
        if (this.getClass().getGenericSuperclass() instanceof ParameterizedType) {
            return (ParameterizedType) this.getClass().getGenericSuperclass();
        }

        return (ParameterizedType) this.getClass().getSuperclass().getGenericSuperclass();
    }

    private Query addPagination(Pageable pageable, Query query) {
        return query.setFirstResult((pageable.getPage() - 1) * pageable.getSize())
                .setMaxResults(pageable.getSize());
    }

    private Long getTotalCount(Root<E> rootEntity, CriteriaQuery<E> entityQuery) {
        final CriteriaQuery<Long> countQuery = this.criteriaBuilder.createQuery(Long.class);

        final Set<Root<?>> availableRoots = BaseRepositoryUtils.getRootsForCriteriaQuery(entityQuery);
        final Set<Root<?>> countQueryRoots = BaseRepositoryUtils.getRootsForCriteriaQuery(countQuery);
        countQueryRoots.addAll(availableRoots);

        countQuery.select(this.criteriaBuilder.count(rootEntity));
        if (entityQuery.getRestriction() != null) {
            countQuery.where(entityQuery.getRestriction());
        }

        TypedQuery<Long> query = this.entityManager.createQuery(countQuery);
        return query.getSingleResult();
    }
}
