package server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import server.entities.UserRole;

public interface IUserRoleRepository extends JpaRepository<UserRole, Integer> {

}
