package server.services.implementations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import server.entities.User;
import server.repositories.IUserRepository;
import server.services.IUserService;

// INITIAL COMMIT: PERSISTENCIA DE PRUEBA - SE BORRARA EN PROXIMAS REVISIONES.

@Service
public class UserService implements IUserService {

	@Autowired
	private IUserRepository repository;

	@Override
	@Transactional(readOnly = true)
	public User findById(int id) {
		
		return repository.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public User findByName(String name) {
		
		return repository.findByName(name);
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getAll() {

		return repository.findAll();
	}

	@Override
	@Transactional
	public boolean insertOrUpdate(User user) {

		// TODO enhance handler response and exception

		return repository.save(user) != null ? true : false;
	}

	@Override
	@Transactional
	public boolean remove(int id) {

		boolean isDeleted = false;

		try {
			repository.deleteById(id);
			isDeleted = true;
		}

		catch (Exception e) {
			// TODO: handle exception
		}

		return isDeleted;
	}

}
