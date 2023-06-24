package br.com.projetodifm.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.projetodifm.model.User;
import br.com.projetodifm.model.Permission;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmailOrPhoneNumber(String email, String phoneNumber);

    Optional<List<User>> findByPermissions(Permission permission); 

    Boolean existsByIdAndPermissions(Long id, Permission permission);
}
