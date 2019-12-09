package com.cyecize.baserepository;

import org.hibernate.query.criteria.internal.CriteriaQueryImpl;
import org.hibernate.query.criteria.internal.QueryStructure;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.Set;

public class BaseRepositoryUtils {

    @SuppressWarnings("unchecked")
    public static Set<Root<?>> getRootsForCriteriaQuery(CriteriaQuery<?> criteriaQuery) {

        try {
            final Field queryStructureField = CriteriaQueryImpl.class.getDeclaredField("queryStructure");
            queryStructureField.setAccessible(true);

            final QueryStructure queryStructure = (QueryStructure) queryStructureField.get(criteriaQuery);

            final Field rootsField = QueryStructure.class.getDeclaredField("roots");
            rootsField.setAccessible(true);

            return (Set<Root<?>>) rootsField.get(queryStructure);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
