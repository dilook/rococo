package qa.guru.rococo.data.repository;

import qa.guru.rococo.data.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {

}