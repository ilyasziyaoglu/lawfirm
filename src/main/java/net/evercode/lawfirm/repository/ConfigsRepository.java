package net.evercode.lawfirm.repository;

import net.evercode.lawfirm.domain.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the Configs entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConfigsRepository extends JpaRepository<Config, Long>, JpaSpecificationExecutor<Config> {
    Optional<Config> findTopByKey(String key);
}
