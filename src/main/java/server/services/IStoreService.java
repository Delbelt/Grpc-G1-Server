package server.services;

import java.util.List;

import server.entities.Store;
import server.entities.User;

public interface IStoreService {
	
	// Método para obtener una tienda por su código
    Store getStoreByCode(String code);

    // Método para obtener todas las tiendas
    List<Store> getAllStores();
    
    // Metodo traer todas las tiendas por estado (habilitado o deshabilitado)
    List<Store> getStoresByState(boolean active);

    // Método para crear una nueva tienda
    Store createStore(Store store);

    // Método para actualizar una tienda existente
    Store updateStore(Store store);

    // Método para eliminar una tienda por su código
    boolean deleteStore(String code);
    
    

	

	
	
}
