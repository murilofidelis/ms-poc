FROM bellsoft/liberica-openjre-alpine:17 AS layertools

WORKDIR /extract

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} application.jar

RUN java -Djarmode=layertools \
    -jar application.jar extract \
    --destination /extract/

FROM bellsoft/liberica-openjre-alpine:17 AS staged

WORKDIR /application

COPY --from=layertools /extract/dependencies/ ./
COPY --from=layertools /extract/spring-boot-loader/ ./
COPY --from=layertools /extract/snapshot-dependencies/ ./
COPY --from=layertools /extract/application/ ./

ENV SPRING_OUTPUT_ANSI_ENABLED=ALWAYS \
    JAVA_OPTS="-server -Xmx256m"\
    PORT=8081\
    SPRING_PROFILES_ACTIVE=cloud

ENTRYPOINT ["java","org.springframework.boot.loader.launch.JarLauncher"]

# docker build -t dev/admin-api:v1 .