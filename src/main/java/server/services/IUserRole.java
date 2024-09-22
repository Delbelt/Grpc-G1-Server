package server.services;

import java.util.List;

import server.entities.UserRole;

public interface IUserRole {

	public UserRole findById(int id);

	public List<UserRole> getAll();

	public boolean insertOrUpdate(UserRole user);

	public boolean remove(int id);
}
