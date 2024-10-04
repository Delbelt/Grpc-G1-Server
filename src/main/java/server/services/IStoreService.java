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

    // Método para habilitar/deshabilitar una tienda
    Store changeStoreState(String code, boolean active);
    
    // Método para actualizar una tienda existente
    Store updateStore(Store store);

    // Método para eliminar una tienda por su código
    boolean deleteStore(String code);
   
    // Método para asignar un producto a una tienda
    void assignProductToStore(String storeCode, String productCode);
    
    // Método para asignar un usuario a una tienda
    void assignUserToStore(String storeCode, int userId);
     
    // Método para desasignar un producto en una tienda
    void removeProductFromStore(String storeCode, String productCode);
     
    // Método para desasignar un usuario en una tienda
    void removeUserFromStore(String storeCode,int userId);
	

	
	
}
