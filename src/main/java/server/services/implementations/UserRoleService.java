package server.services.implementations;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import server.entities.UserRole;
import server.repositories.IUserRoleRepository;
import server.services.IUserRole;

@Slf4j
@Service
public class UserRoleService implements IUserRole {
	
	@Autowired
	private IUserRoleRepository repository;
	
	@Override
	@Transactional(readOnly = true)
	public UserRole findById(int id) {
		
		var response = repository.findById(id).orElse(null);
		
		log.info("[UserRoleService][findById]: " + response);
		
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserRole> getAll() {

		return repository.findAll();
	}

	@Override
	@Transactional
	public boolean insertOrUpdate(UserRole userRole) {

		// TODO enhance handler response and exception

		return repository.save(userRole) != null ? true : false;
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
