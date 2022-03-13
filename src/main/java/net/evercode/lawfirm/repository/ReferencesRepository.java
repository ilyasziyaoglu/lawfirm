package net.evercode.lawfirm.repository;

import net.evercode.lawfirm.domain.References;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the References entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ReferencesRepository extends JpaRepository<References, Long> {}
