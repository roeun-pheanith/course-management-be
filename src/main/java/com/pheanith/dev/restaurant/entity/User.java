package com.pheanith.dev.restaurant.entity;

import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String firstName;
	private String lastName;
	private String username;
	private String password;
	private String email;
	private boolean accountNonExpired;
	private boolean accountNonLock;
	private boolean credentialsNonExpired;
	private boolean enable;

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Role> roles;

	@ManyToMany(fetch = FetchType.EAGER)
	private List<Enrollment> enrolls;

	public User(String username, String email, String password) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.accountNonExpired = true;
		this.accountNonLock = true;
		this.credentialsNonExpired = true;
		this.enable = true;
	}

}
