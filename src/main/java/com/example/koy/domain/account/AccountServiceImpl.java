package com.example.koy.domain.account;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.koy.domain.account.dto.request.LoginRequestDto;
import com.example.koy.domain.account.dto.request.SignUpRequestDto;
import com.example.koy.domain.account.dto.response.LoginResponseDto;
import com.example.koy.domain.account.dto.response.SignUpResponseDto;
import com.example.koy.entity.Admin;
import com.example.koy.repository.AdminRepository;
import com.example.koy.util.CommonService;
import com.example.koy.util.errors.ErrorCode;
import com.example.koy.util.errors.exception.RestApiException;
import com.example.koy.util.security.JwtProvider;

import de.mkammerer.argon2.Argon2Factory;
import jakarta.servlet.http.HttpServletRequest;

@Service
@Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
public class AccountServiceImpl implements AccountService {
	private final JwtProvider jwtProvider;
	private final Random random = SecureRandom.getInstanceStrong();
	private final Map<String, Integer> adminLockCount = new LinkedHashMap<>();
	private final AdminRepository adminRepository;

	@Autowired
	public AccountServiceImpl(JwtProvider jwtProvider, AdminRepository adminRepository) throws
		NoSuchAlgorithmException {
		this.jwtProvider = jwtProvider;
		this.adminRepository = adminRepository;
	}

	@Override
	public SignUpResponseDto signup(SignUpRequestDto signUpRequestDto) throws RestApiException {
		checkAdminAndAdminGroupExist(signUpRequestDto.getEmployeeNumber(), signUpRequestDto.getGroupId());

		if (signUpRequestDto.getGroupId() == 1) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("group_id", "invalid");
			throw new RestApiException(ErrorCode.INVALID_ARGUMENT_ERROR, jsonObject,
				"해당 그룹으로 관리자를 추가할 수 없습니다. 관리자에게 문의하세요.");
		}

		String hashPassword = argon2Hash(signUpRequestDto.getPassword());
		signUpRequestDto.setPasswordHash(hashPassword);

		adminRepository.save(Admin
			.builder()
			.employeeNumber(signUpRequestDto.getEmployeeNumber())
			.name(signUpRequestDto.getName())
			.email(signUpRequestDto.getEmail())
			.department(signUpRequestDto.getDepartment())
			.position(signUpRequestDto.getPosition())
			.password(hashPassword)
			.phoneNumber(signUpRequestDto.getPhoneNumber())
			.lastUseTime(Timestamp.valueOf(LocalDateTime.now()))
			.requirePasswordReset(true)
			.build()
		);

		return SignUpResponseDto.fromEntity(true);
	}

	@Override
	public LoginResponseDto login(HttpServletRequest request, LoginRequestDto loginRequest) throws
		RestApiException,
		UnsupportedEncodingException,
		UnknownHostException {
		Admin admin = adminRepository.findAdminByEmployeeNumber(loginRequest.getEmployeeNumber()).orElseThrow(() -> {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("employee_number", "not_exist");
			return new RestApiException(ErrorCode.UNAUTHORIZED_ERROR, jsonObject, "아이디 또는 비밀번호가 맞지 않습니다.");
		});

		if (admin.getLock()) {
			throw new RestApiException(ErrorCode.FORBIDDEN_ERROR, "Admin locked", "계정이 잠겼습니다.");
		}

		String adminLockCountKey = String.format("lock:%s", admin.getId());
		Integer adminLockCountValue = adminLockCount.get(adminLockCountKey);
		if (adminLockCountValue == null) {
			adminLockCountValue = 0;
		}

		String ip = CommonService.getRequestIp(request);

		Boolean passwordVerify = argon2Verify(admin.getPassword(), loginRequest.getPassword());
		if (!passwordVerify) {
			adminLockCountValue += 1;
			adminLockCount.put(adminLockCountKey, adminLockCountValue);
			if (adminLockCountValue >= 5) {
				admin.setLock(true);
				adminLockCount.remove(adminLockCountKey);
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("password", "invalid");
			throw new RestApiException(ErrorCode.UNAUTHORIZED_ERROR, jsonObject, "아이디 또는 비밀번호가 맞지 않습니다.");
		}

		if (admin.getRequirePasswordReset()) {
			return LoginResponseDto.fromEntity(null, true);
		} else {
			String token = createJwtToken(admin);

			// 마지막 사용시간 업데이트 (변경 감지 dirty check)
			admin.setLastUseTime(Timestamp.valueOf(LocalDateTime.now()));

			return LoginResponseDto.fromEntity(token, false);
		}
	}

	/**
	 * 관리자 존재 여부 확인
	 * @param employeeNumber String
	 * @param groupId String
	 */
	private void checkAdminAndAdminGroupExist(String employeeNumber, Integer groupId) throws RestApiException {
		boolean isEmployeeNumberExist = adminRepository.existsAdminByEmployeeNumber(employeeNumber);
		if (isEmployeeNumberExist) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("employee_number", "duplicated");
			throw new RestApiException(ErrorCode.DUPLICATED_ERROR, jsonObject, "해당 사원번호 관리자가 이미 존재합니다.");
		}
	}

	/**
	 * JWT 토큰 생성
	 * @param admin Admin
	 * @return String - 토큰 반환
	 */
	private String createJwtToken(Admin admin) throws UnsupportedEncodingException {
		return jwtProvider.createToken(admin.getId().toString(), admin.getEmployeeNumber(),
			admin.getSessionExpireTime());
	}

	/**
	 * 랜덤 문자열 반환 함수
	 * @return String - 랜덤 문자열 반환 (특수문자 + 대소문자 + 숫자)
	 */
	private String getRandomPassword() {
		StringBuilder strPwd = new StringBuilder();
		char[] str = new char[1];

		for (int i = 0; i < 15; i++) {
			str[0] = (char)(random.nextInt(84) + 42);
			strPwd.append(str);
		}

		return strPwd.toString();
	}

	/**
	 * argon encode 함수
	 * @param str String
	 * @return String - 인코딩 문자열
	 */
	private String argon2Hash(String str) {
		return Argon2Factory.create().hash(3, 4096, 1, str.toCharArray());
	}

	/**
	 * argon verify 함수
	 * @param hash String
	 * @param str String
	 * @return String - 일치 여부
	 */
	private Boolean argon2Verify(String hash, String str) {
		return Argon2Factory.create().verify(hash, str.toCharArray());
	}
}
