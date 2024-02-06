package com.example.koy.repository.postgres;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.koy.entity.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {
	Optional<Admin> findAdminByEmployeeNumber(String employeeNumber);

	void deleteByEmployeeNumberIn(List<String> employeeNumber);

	boolean existsAdminByEmployeeNumber(String employeeNumber);

	@Query("SELECT a FROM Admin a JOIN FETCH a.adminGroup WHERE a.employeeNumber = :employeeNumber")
	Optional<Admin> findAdminByEmployeeNumberFetchJoin(String employeeNumber);
}
