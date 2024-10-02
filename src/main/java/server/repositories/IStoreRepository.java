package server.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import server.entities.Store;

public interface IStoreRepository extends JpaRepository<Store, String>{
	
	
    
	@Query("SELECT s FROM Store s LEFT JOIN FETCH s.stocks LEFT JOIN FETCH s.users WHERE s.code = :code")
	Store findByCode(@Param("code") String code);
    
    
	//Traer todas las tiendas por estado habilitado o deshabilitado
	@Query("SELECT s FROM Store s LEFT JOIN FETCH s.stocks LEFT JOIN FETCH s.users WHERE s.active = :active")
	List<Store> findByActive(@Param("active") boolean active);
	
	
}
