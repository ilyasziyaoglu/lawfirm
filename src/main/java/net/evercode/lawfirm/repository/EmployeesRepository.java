package net.evercode.lawfirm.repository;

import net.evercode.lawfirm.domain.Employees;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the Employees entity.
 */
@Repository
public interface EmployeesRepository
    extends EmployeesRepositoryWithBagRelationships, JpaRepository<Employees, Long>, JpaSpecificationExecutor<Employees> {

    Optional<Employees> findTopById(Long id);

    default Optional<Employees> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Employees> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Employees> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct employees from Employees employees left join fetch employees.servicePoint",
        countQuery = "select count(distinct employees) from Employees employees"
    )
    Page<Employees> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct employees from Employees employees left join fetch employees.servicePoint")
    List<Employees> findAllWithToOneRelationships();

    @Query("select employees from Employees employees left join fetch employees.servicePoint where employees.id =:id")
    Optional<Employees> findOneWithToOneRelationships(@Param("id") Long id);
}
