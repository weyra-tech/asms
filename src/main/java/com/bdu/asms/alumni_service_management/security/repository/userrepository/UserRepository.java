package com.bdu.asms.alumni_service_management.security.repository.userrepository;


import com.bdu.asms.alumni_service_management.security.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByPublicId(String publicId);
    Optional<User> findByUserNameIgnoreCase(String userName);
    boolean existsByRolesPublicId(String publicId);


}
