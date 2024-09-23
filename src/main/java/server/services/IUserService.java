package server.services;

import java.util.List;

//INITIAL COMMIT: PERSISTENCIA DE PRUEBA - SE BORRARA EN PROXIMAS REVISIONES.

import server.entities.User;

public interface IUserService {
	
	public User findById(int id);

	public User findByUserName(String userName);

	public List<User> getAll();

	public boolean insertOrUpdate(User user);

	public boolean remove(int id);
}
