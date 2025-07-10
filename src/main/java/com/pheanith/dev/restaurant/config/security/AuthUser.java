package com.pheanith.dev.restaurant.config.security;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class AuthUser implements UserDetails {

	private String username;
	private String password;
	private Set<SimpleGrantedAuthority> authorities;
	private boolean accountNonExpired;
	private boolean accountNonLock;
	private boolean credentialNonExpired;
	private boolean enable;

	@Override
	public String getUsername() {

		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}
	
	public boolean isAccountNonLock() {
		return accountNonLock;
	}
	
	public boolean isCredentialNonExpired() {
		return credentialNonExpired;
	}
	
	public boolean isEnable() {
		return enable;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

}
