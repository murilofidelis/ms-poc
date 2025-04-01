package com.mfm.user.user_service.service.impl;

import com.mfm.user.user_service.client.AccessClient;
import com.mfm.user.user_service.client.dto.DAccess;
import com.mfm.user.user_service.domain.User;
import com.mfm.user.user_service.domain.dto.DUser;
import com.mfm.user.user_service.domain.dto.DUserUpdate;
import com.mfm.user.user_service.exception.BusinessException;
import com.mfm.user.user_service.exception.MessageKey;
import com.mfm.user.user_service.repository.UserRepository;
import com.mfm.user.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    @Override
    public DUser update(int id, DUserUpdate userUpdate) {

        Optional<User> userOptional = repository.findById(id);

        if (userOptional.isEmpty()) {
            throw new BusinessException(MessageKey.USER_NOT_FOUND);
        }

        User user = userOptional.get();
        user.setName(userUpdate.getName());
        user.setEmail(userUpdate.getEmail());
        User userSave = repository.save(user);
        return new DUser(userSave);
    }

    private DAccess createAccess(DUser user) {
        var accessRequest = new DAccess(user.getUserName(), user.getPassword());
        return client.createAccess(accessRequest);
    }
}
