package net.evercode.lawfirm.repository;

import net.evercode.lawfirm.domain.Employees;
import org.hibernate.annotations.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class EmployeesRepositoryWithBagRelationshipsImpl implements EmployeesRepositoryWithBagRelationships {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Optional<Employees> fetchBagRelationships(Optional<Employees> employees) {
        return employees.map(this::fetchServices);
    }

    @Override
    public Page<Employees> fetchBagRelationships(Page<Employees> employees) {
        return new PageImpl<>(fetchBagRelationships(employees.getContent()), employees.getPageable(), employees.getTotalElements());
    }

    @Override
    public List<Employees> fetchBagRelationships(List<Employees> employees) {
        return Optional.of(employees).map(this::fetchServices).get();
    }

    Employees fetchServices(Employees result) {
        return entityManager
            .createQuery(
                "select employees from Employees employees left join fetch employees.services where employees is :employees",
                Employees.class
            )
            .setParameter("employees", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Employees> fetchServices(List<Employees> employees) {
        return entityManager
            .createQuery(
                "select distinct employees from Employees employees left join fetch employees.services where employees in :employees",
                Employees.class
            )
            .setParameter("employees", employees)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
    }
}
