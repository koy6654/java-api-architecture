package com.example.koy.util.security;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.koy.entity.Admin;
import com.example.koy.repository.AdminRepository;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
	private final AdminRepository adminRepository;

	@Autowired
	public UserDetailsServiceImpl(AdminRepository adminRepository) {
		this.adminRepository = adminRepository;
	}

	@Override
	@Transactional(transactionManager = "transactionManager")
	public UserDetails loadUserByUsername(String employeeNumber) throws UsernameNotFoundException {
		Admin admin = adminRepository.findAdminByEmployeeNumberFetchJoin(employeeNumber).orElseThrow(() -> {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("employee_number", "not_exist");
			return new UsernameNotFoundException("Can't found admin with this jwt token.");
		});

		return new UserDetailsImpl(admin);
	}
}
