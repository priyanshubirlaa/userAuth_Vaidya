package com.spring.vaidya.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.spring.vaidya.entity.ConfirmTokenDoctor;

/**
 * Repository interface for handling database operations related to
 * ConfirmTokenDoctor entity.
 */
@Repository("confirmTokenDoctorRepo")
public interface ConfirmTokenDoctorRepo extends JpaRepository<ConfirmTokenDoctor, Long> {

    /**
     * Finds a ConfirmTokenDoctor entity by the confirmation token.
     *
     * @param confirmToken The confirmation token string.
     * @return The corresponding ConfirmTokenDoctor entity, if found.
     */
    ConfirmTokenDoctor findByConfirmTokenDoctor(String confirmToken);
}
