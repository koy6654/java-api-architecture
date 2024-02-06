package com.probitkorea.koy.entity;

import java.sql.Timestamp;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@DynamicUpdate
public class Admin {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@ColumnDefault(value = "gen_random_uuid()")
	private UUID id;

	@Column(nullable = false, unique = true)
	private String employeeNumber;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String department;

	@Column(nullable = false)
	private String position;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String phoneNumber;

	@Column(nullable = false)
	@ColumnDefault(value = "false")
	private Boolean lock;

	@Column(nullable = false)
	@ColumnDefault(value = "false")
	private Boolean deactivate;

	@Column(nullable = false)
	@ColumnDefault(value = "false")
	private Boolean deleted;

	@Column()
	private Timestamp deleteTime;

	@Column(nullable = false)
	@ColumnDefault(value = "true")
	private Boolean requirePasswordReset;

	@Column(nullable = false, columnDefinition = "integer check (session_expire_time in (15, 30, 45, 60))")
	@ColumnDefault(value = "30")
	private Integer sessionExpireTime;

	@Column()
	private Timestamp lastUseTime;

	@Builder
	public Admin(String employeeNumber, String name, String email, String department, String position, String password,
				 String phoneNumber, Boolean lock, Boolean deactivate, Boolean deleted, Timestamp deleteTime,
				 Boolean requirePasswordReset, Timestamp lastUseTime) {
		this.employeeNumber = employeeNumber;
		this.name = name;
		this.email = email;
		this.department = department;
		this.position = position;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.lock = lock;
		this.deactivate = deactivate;
		this.deleted = deleted;
		this.deleteTime = deleteTime;
		this.requirePasswordReset = requirePasswordReset;
		this.lastUseTime = lastUseTime;
	}
}
