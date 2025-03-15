package com.mfm.user.user_service.exception;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

//@ControllerAdvice
public class ExceptionHandling extends ResponseEntityExceptionHandler {

  //  @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> exceptionHandler(Exception exception, NativeWebRequest request) {
        AppErrorBuilder error = AppErrorBuilder.builder(exception, request)
                .comStatus()
                .comTitulo()
                .comUrl()
                .comTipoMetodoHttp()
                .comDetalhe()
                .comCausa()
                .comDadosUsuarioLogado()
                .comParametrosDaRequisicao()
                .comInfoClasseGerouErro()
                .comStackTrace(3)
                .build();
        return handleExceptionInternal(exception, error, new HttpHeaders(), error.getHttpStatus(), request);
    }
}
