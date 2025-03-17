package com.mfm.user.user_service.service.impl;

import com.mfm.user.user_service.client.AccessClient;
import com.mfm.user.user_service.client.dto.DAccess;
import com.mfm.user.user_service.domain.User;
import com.mfm.user.user_service.domain.dto.DUser;
import com.mfm.user.user_service.repository.UserRepository;
import com.mfm.user.user_service.service.UserService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AccessClient client;
    private final UserRepository repository;

    @Override
    public DUser create(DUser user) {
        var accessRequest = createAccess(user);

        User newUser = new User(user);
        newUser.setIdAccess(accessRequest.getId());

        User userSave = repository.save(newUser);

        return new DUser(userSave);
    }

    private DAccess createAccess(DUser user) {
        var accessRequest = new DAccess(user.getUserName(), user.getPassword());
        try {
            return client.createAccess(accessRequest);
        } catch (FeignException.FeignClientException e) {
            if (e.status() == HttpStatus.BAD_REQUEST.value()) {
                log.error("User exists");
                throw e;
            }
            throw e;
        } catch (RuntimeException ex) {
            throw ex;
        }
    }
}
