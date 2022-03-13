package net.evercode.lawfirm.repository;

import net.evercode.lawfirm.domain.ServicePoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ServicePoints entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ServicePointsRepository extends JpaRepository<ServicePoints, Long> {}
