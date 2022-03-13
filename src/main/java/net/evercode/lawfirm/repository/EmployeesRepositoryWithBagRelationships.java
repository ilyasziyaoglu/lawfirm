package net.evercode.lawfirm.repository;

import net.evercode.lawfirm.domain.Employees;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface EmployeesRepositoryWithBagRelationships {
    Optional<Employees> fetchBagRelationships(Optional<Employees> employees);

    List<Employees> fetchBagRelationships(List<Employees> employees);

    Page<Employees> fetchBagRelationships(Page<Employees> employees);
}
