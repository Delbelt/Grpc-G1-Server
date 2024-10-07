package server.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.google.common.base.Optional;

import server.entities.Product;
import server.entities.Stock;

public interface IStockRepository extends JpaRepository<Stock, String> {
	
	// Encuentra por id de stock
	public Stock findByCode(String code);
	// Encuentra todos los stock en una tienda especifica
    List<Stock> findByStore_Code(String codeStore);
    // Encuentra todos los stock disponibles (Cantidad mayor a 0)
    List<Stock> findByQuantityGreaterThan(int quantity);
    // Encuentra todos los stock no disponibles (Cantidad = 0)
    List<Stock> findByQuantity(int quantity);
    // Encuentra todos los stocks que contengan el mismo producto
    List<Stock> findByProduct_Code(String productCode);
    // Verifica si existe un stock por codeProduct y codeStore
    Optional<Stock> findByProduct_CodeAndStore_Code(String productCode, String storeCode);

    @Query("SELECT s FROM Stock s WHERE s.store.code = :codeStore AND s.product.code = :codeProduct")
    public Stock findByStoreAndProduct(String codeStore, String codeProduct);
}