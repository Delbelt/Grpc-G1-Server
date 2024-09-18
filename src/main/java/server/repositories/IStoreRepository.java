package server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import server.entities.Store;

public interface IStoreRepository extends JpaRepository<Store, String>{
	Store findByCode(String code);
}
