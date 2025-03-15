package com.mfm.user.user_service.exception;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public class AppErrorBuilder {

    private static final String REQUEST_PARAMS = "params";

    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    private Integer status = httpStatus.value();
    private String title;
    private String detail;
    private String metodoHttp;
    private String path;
    private String dataUser;
    private String className;
    private String methodName;
    private int numLineErro;
    private String exceptionName;
    private Map<String, Object> extrasParams = new LinkedHashMap<>();
    private String cause;
    private String stackTraceCause;
    private String stack;

    @JsonIgnore
    private NativeWebRequest request;

    @JsonIgnore
    private Exception exception;

    @JsonIgnore
    private final List<Integer> alertas = Arrays.asList(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.UNAUTHORIZED.value(),
            HttpStatus.FORBIDDEN.value(),
            HttpStatus.NOT_FOUND.value());

    private AppErrorBuilder() {
    }


    protected static AppErrorBuilder builder(Exception exception, NativeWebRequest request) {
        return new AppErrorBuilder(exception, request);
    }

    private AppErrorBuilder(Exception exception, NativeWebRequest request) {
        this.exception = exception;
        this.request = request;
    }

    protected AppErrorBuilder comStatus() {
        if (this.exception != null) {
            ResponseStatus responseStatus = this.exception.getClass().getAnnotation(ResponseStatus.class);
            if (responseStatus != null) {
                this.httpStatus = responseStatus.value();
                this.status = httpStatus.value();
            }
        }
        return this;
    }

    protected AppErrorBuilder comTitulo() {
        if (this.exception != null) {
            this.title = this.httpStatus.getReasonPhrase();
        }
        return this;
    }

    protected AppErrorBuilder comDadosUsuarioLogado() {
        if (this.request != null && request.getUserPrincipal() != null) {
            this.dataUser = request.getUserPrincipal().getName();
        }
        return this;
    }

    protected AppErrorBuilder comTipoMetodoHttp() {
        if (this.request != null) {
            this.metodoHttp = ((ServletWebRequest) this.request).getRequest().getMethod();
        }
        return this;
    }

    protected AppErrorBuilder comDetalhe() {
        if (this.exception != null) {
            this.detail = this.exception.getMessage();
        }
        return this;
    }

    protected AppErrorBuilder comUrl() {
        if (this.request != null) {
            this.path = ((ServletWebRequest) this.request).getRequest().getRequestURI();
        }
        return this;
    }

    protected AppErrorBuilder comCausa() {
        if (this.exception != null && this.exception.getCause() != null) {
            this.cause = this.exception.getCause().getClass().getName();
            this.stackTraceCause = this.getStackTrace(this.exception.getCause().getStackTrace(), 3);
        }
        return this;
    }

    protected AppErrorBuilder comParametrosDaRequisicao() {
        if (this.request != null) {
            Map<String, String> map = new LinkedHashMap<>();
            Map<String, String[]> parametrosRequisicao = ((ServletWebRequest) request).getRequest().getParameterMap();
            for (Map.Entry<String, String[]> parameto : parametrosRequisicao.entrySet()) {
                String[] valorParametro = parameto.getValue();
                String valor = null;
                if (valorParametro != null && valorParametro.length == 1) {
                    valor = valorParametro[0];
                } else if (valorParametro != null && valorParametro.length > 1) {
                    valor = Arrays.toString(valorParametro);
                }
                map.put(parameto.getKey(), valor);
            }
            this.extrasParams.put(REQUEST_PARAMS, map);
        }
        return this;
    }

    protected AppErrorBuilder comInfoClasseGerouErro() {
        StackTraceElement[] stackTrace = null;
        if (this.exception != null) {
            stackTrace = this.exception.getStackTrace();
            this.exceptionName = this.exception.getClass().getName();
        }
        if (stackTrace != null && stackTrace.length > 0) {
            StackTraceElement stackTraceElement = stackTrace[0];
            this.className = stackTraceElement.getClassName();
            this.methodName = stackTraceElement.getMethodName();
            this.numLineErro = stackTraceElement.getLineNumber();
        }
        return this;
    }

    protected AppErrorBuilder comStackTrace(int numLinhas) {
        this.adicionaStackTrace(numLinhas);
        return this;
    }

    protected AppErrorBuilder comStackTrace() {
        this.adicionaStackTrace(null);
        return this;
    }

    protected AppErrorBuilder build() {
        if (this.isAlerta()) {
            log.warn("\n-------------------------------------------------------- ALERTA: " + this.status + " ----------------------------------------------------------------------\t" +
                    "\n Detalhes: \n" + this.toString());
            log.warn("\n---------------------------------------------------------------------------------------------------------------------------------------------");
        } else {
            log.error("\n-------------------------------------------------------- ERRO: " + this.status + " ----------------------------------------------------------------------\t" +
                    "\n Detalhes do erro: \n" + this.toString());
            log.error("StackTrace:", this.exception);
            log.error("\n---------------------------------------------------------------------------------------------------------------------------------------------");
        }
        return this;
    }

    private boolean isAlerta() {
        return this.status != null && this.alertas.contains(this.status);
    }

    private void adicionaStackTrace(Integer numLinhas) {
        if (this.isAlerta()) {
            return;
        }
        if (this.exception != null) {
            this.stack = this.getStackTrace(this.exception.getStackTrace(), numLinhas);
        }
    }

    private String getStackTrace(StackTraceElement[] stack, Integer numLinhas) {
        if (stack == null) {
            return null;
        }
        StringWriter sw = new StringWriter();
        printStackTrace(stack, new PrintWriter(sw), numLinhas);
        return sw.toString();
    }

    private void printStackTrace(StackTraceElement[] stackTrace, PrintWriter printWriter, Integer numLinhas) {
        if (numLinhas != null) {
            for (int i = 0; i <= numLinhas; i++) {
                if (stackTrace.length > i) {
                    printWriter.println(stackTrace[i]);
                }
            }
        } else {
            for (StackTraceElement stackTraceEl : stackTrace) {
                printWriter.println(stackTraceEl);
            }
        }
    }

    @Override
    public String toString() {
        return "\n{" +
                "\n status=" + status + '\'' +
                ",\n titulo='" + title + '\'' +
                ",\n detalhe='" + detail + '\'' +
                ",\n metodoHttp='" + metodoHttp + '\'' +
                ",\n uri='" + path + '\'' +
                ",\n usuarioLogado='" + dataUser + '\'' +
                ",\n nomeClasse='" + className + '\'' +
                ",\n nomeMetodo='" + methodName + '\'' +
                ",\n numLinhaErro=" + numLineErro +
                ",\n nomeExecao='" + exceptionName + '\'' +
                '}';
    }
}