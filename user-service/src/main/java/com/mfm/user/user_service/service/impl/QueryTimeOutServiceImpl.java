package com.mfm.user.user_service.service.impl;

import com.mfm.user.user_service.repository.QueryTimeOutRepository;
import com.mfm.user.user_service.service.QueryTimeOutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryTimeOutServiceImpl implements QueryTimeOutService {

    private final QueryTimeOutRepository repository;

    public String testTimeOut() {
        repository.getTime();
        return "success";
    }

    public String testTimeOutCustom() {
        repository.getTimeCustom();
        return "success";
    }
}
