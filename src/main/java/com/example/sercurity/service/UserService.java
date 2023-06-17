package com.example.sercurity.service;

import com.example.sercurity.domain.Users;
import com.example.sercurity.exception.AppException;
import com.example.sercurity.exception.ErrorCode;
import com.example.sercurity.repository.UserRepository;
import com.example.sercurity.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}")
    private String key;

    private Long expireTimeMs = 1000 * 60 * 60l;

    public String join(String userName, String password){

        //userName 중복 check
        userRepository.findByUserName(userName)
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.USERNAME_DUPLICATED, userName + "는 이미 있습니다.");
                });

        //저장
        Users user = Users.builder()
                .userName(userName)
                .password(encoder.encode(password))
                .build();
        userRepository.save(user);

        return "SUCCESS";
    }

    public String login(String userName, String password){
        // userName 없음
        Users selectedUser =  userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "이 없습니다."));

        // password 틀림
        log.info("selectedPw: {} pw: {}" , selectedUser.getPassword(), password);
        if(!encoder.matches(password, selectedUser.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD, "패스워드를 잘 못 입력하였습니다.");
        }

        // 로그인 성공 token 발행
        String token = JwtTokenUtil.createToken(selectedUser.getUserName(), key, expireTimeMs);

        return token;
    }
}
