package server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import server.entities.User;

//INITIAL COMMIT: PERSISTENCIA DE PRUEBA - SE BORRARA EN PROXIMAS REVISIONES.

public interface IUserRepository extends JpaRepository<User, Integer> {
	
	public User findByName(String name);
}
