package com.example.dormmatching.service.auth;


import com.example.dormmatching.dto.LoginRequest;
import com.example.dormmatching.dto.LoginResponse;
import com.example.dormmatching.dto.RegisterRequest;
import com.example.dormmatching.entity.support.Department;
import com.example.dormmatching.entity.support.Region;
import com.example.dormmatching.entity.user.Role;
import com.example.dormmatching.entity.user.StudentStatus;
import com.example.dormmatching.entity.user.User;
import com.example.dormmatching.repository.*;
import com.example.dormmatching.service.JwtService;
import com.example.dormmatching.service.RegionResolutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final StudentStatusRepository statusRepo;
    private final RoleRepository roleRepo;
    private final RegionRepository regionRepo;
    private final RegionResolutionService regionResolver;  // 새로 주입
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final DepartmentRepository departmentRepo;

    public void register(RegisterRequest req) {
        // 중복 학번 체크
        if (userRepository.findByStudentNumber(req.getStudentNumber()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 학번입니다.");
        }

        StudentStatus status = statusRepo.findById(req.getStatusId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 학적 상태입니다."));
        Role role = roleRepo.findById(req.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 역할입니다."));
        Department dept = departmentRepo.findById(req.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 학과입니다."));


        // 1) 주소 기반 regionId 결정
        int resolvedRegionId = regionResolver.resolveRegionId(req.getAddress());
        Region region = regionRepo.findById(resolvedRegionId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 지역입니다."));

        User user = new User();
        user.setStudentNumber(req.getStudentNumber());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setName(req.getName());
        user.setStatus(status);
        user.setRole(role);
        user.setRegion(region);
        user.setBirthDate(req.getBirthDate());
        user.setGender(req.getGender());
        user.setAddress(req.getAddress());
        user.setInternational(req.getInternational());
        user.setPhoneNumber(req.getPhoneNumber());
        user.setDepartment(dept);

        // 초기 비밀번호 설정 로직 등 나머지 처리...
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest req) {
        // 1) 인증 시도
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getStudentNumber(),
                        req.getPassword()
                )
        );

        User user = userRepository.findByStudentNumber(req.getStudentNumber())
                .orElseThrow();

        String accessToken  = jwtService.generateToken(req.getStudentNumber());
        String refreshToken = jwtService.generateRefreshToken(req.getStudentNumber());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        boolean init = user.isPasswordInitialized();
        return new LoginResponse(accessToken, refreshToken, init);
    }

    public void logout(String studentNumber) {
        User user = userRepository.findByStudentNumber(studentNumber)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.setRefreshToken(null);
        userRepository.save(user);
    }
}