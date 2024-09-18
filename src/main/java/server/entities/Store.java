package server.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity 
@Table(name = "store")
public class Store {
	@Id
    @Column(name = "code", unique = true, nullable = false)
    private String code; 

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "province", nullable = false)
    private String province;

    @Column(name = "active")
    private boolean active;

    //@OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    //private List<Stock> stocks;

    
}
