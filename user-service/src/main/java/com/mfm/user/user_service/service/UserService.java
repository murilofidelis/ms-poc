package com.mfm.user.user_service.service;

import com.mfm.user.user_service.domain.dto.DUser;
import com.mfm.user.user_service.domain.dto.DUserUpdate;

public interface UserService {

    DUser create(DUser user);

    DUser update(int id, DUserUpdate user);
}
