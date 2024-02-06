package com.example.koy.util.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.koy.entity.Admin;

public class UserDetailsImpl implements UserDetails {
	private final Admin admin;

	public UserDetailsImpl(Admin admin) {
		this.admin = admin;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(() -> admin.getAdminGroup().getAdminRole().getRoleName());
		return authorities;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return admin.getId().toString();
	}

	@Override
	public boolean isAccountNonExpired() { // 계정 만료 여부 (사용 X)
		return false;
	}

	@Override
	public boolean isAccountNonLocked() { // 계정 잠김 여부 (사용 X)
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() { // 계정 패스워드 만료 여부 (사용 X)
		return false;
	}

	@Override
	public boolean isEnabled() { // 계정 활성화 여부 (사용 X)
		return false;
	}

	public UUID getAdminUid() {
		return admin.getId();
	}
}
