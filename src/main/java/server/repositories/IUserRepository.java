package server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import server.entities.User;

public interface IUserRepository extends JpaRepository<User, Integer> {
	
	public User findByUserName(String username);	
}
