package net.evercode.lawfirm.repository;

import net.evercode.lawfirm.domain.Properties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Properties entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PropertiesRepository extends JpaRepository<Properties, Long>, JpaSpecificationExecutor<Properties> {}
