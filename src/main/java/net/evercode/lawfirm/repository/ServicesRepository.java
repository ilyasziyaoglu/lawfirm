package net.evercode.lawfirm.repository;

import net.evercode.lawfirm.domain.Services;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Services entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ServicesRepository extends JpaRepository<Services, Long> {}
