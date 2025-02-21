package com.spring.vaidya.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.spring.vaidya.entity.PasswordResetToken;
import com.spring.vaidya.entity.User;

/**
 * Repository interface for managing password reset tokens.
 * Provides CRUD operations using JpaRepository.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Finds a PasswordResetToken entity by its token value.
     *
     * @param token The password reset token.
     * @return An Optional containing the PasswordResetToken entity if found, otherwise empty.
     */
    Optional<PasswordResetToken> findByToken(String token);

	//PasswordResetToken findByUser(User user);
	
	 PasswordResetToken findByUser(User user);
}
