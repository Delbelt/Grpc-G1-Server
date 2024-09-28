package server.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import server.entities.Store;

public interface IStoreRepository extends JpaRepository<Store, String>{
	Store findByCode(String code);
	
	//Traer todas las tiendas por estado habilitado o deshabilitado
	List<Store> findByActive(boolean active);
}
