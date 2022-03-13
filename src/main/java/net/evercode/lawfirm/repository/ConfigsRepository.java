package net.evercode.lawfirm.repository;

import net.evercode.lawfirm.domain.Configs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Configs entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConfigsRepository extends JpaRepository<Configs, Long>, JpaSpecificationExecutor<Configs> {}
