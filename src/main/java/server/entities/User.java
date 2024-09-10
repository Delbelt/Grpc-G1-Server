package server.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

// INITIAL COMMIT: PERSISTENCIA DE PRUEBA - SE BORRARA EN PROXIMAS REVISIONES.

@Data // boilerplate
@Entity // Para que se considere entidad
@Table(name="user") // nombre que tendra en la base de datos
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name="name", nullable=false)
	private String name;

}
