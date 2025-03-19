package com.mfm.user.user_service.client.decode;

import com.mfm.user.user_service.client.dto.ApiError;
import com.mfm.user.user_service.util.JsonUtil;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;

public class AccessErrorDecoder implements ErrorDecoder {


    @Override
    public Exception decode(String methodKey, Response response) {

        try (InputStream bodyIs = response.body().asInputStream()) {

            ApiError accessApiError = JsonUtil.toObject(bodyIs, ApiError.class);


        } catch (IOException e) {
            return new Exception(e.getMessage());
        }


        return null;
    }

}
