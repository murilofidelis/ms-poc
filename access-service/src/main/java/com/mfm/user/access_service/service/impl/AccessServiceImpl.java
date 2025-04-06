package com.mfm.user.access_service.service.impl;

import com.mfm.user.access_service.domain.Access;
import com.mfm.user.access_service.domain.dto.DAccess;
import com.mfm.user.access_service.exception.UserExistsException;
import com.mfm.user.access_service.repository.AccessRepository;
import com.mfm.user.access_service.service.AccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessServiceImpl implements AccessService {

    private final AccessRepository repository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public DAccess create(DAccess access) {
        validate(access);
        Access accessBuilder = Access.builder()
                .userName(access.getUserName())
                .password(encryptPass(access.getPassword()))
                .build();
        Access accessSave = repository.save(accessBuilder);
        return new DAccess(accessSave.getId());
    }

    private void validate(DAccess access) {
        boolean exists = repository.exists(access.getUserName());
        if (exists) {
            throw new UserExistsException();
        }
    }

    private String encryptPass(String password) {
        return encoder.encode(password);
    }
}
