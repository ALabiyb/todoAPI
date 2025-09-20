package tz.co.softnet.softcms.frontendapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tz.co.softnet.softcms.frontendapi.models.LoanApplication;

import java.util.Optional;


public interface LoanApplicationRepository  extends JpaRepository<LoanApplication, Long> {
    Optional<LoanApplication> findByNin(String nin);
}
