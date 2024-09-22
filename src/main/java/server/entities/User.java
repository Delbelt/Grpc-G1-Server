package server.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="user")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name="username", nullable=false)
	private String userName;
	
	@Column(name="password", nullable=false)
	private String password;
	
	@Column(name="name", nullable=false)
	private String name;
	
	@Column(name="surname", nullable=false)
	private String surname;
	
	@ManyToOne(cascade=CascadeType.PERSIST) 
	@JoinColumn(name="idRole")
	private UserRole role;
	
	@Column(name="active", nullable=false)
	private boolean active;
	

}
