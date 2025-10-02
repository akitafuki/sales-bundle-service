FROM gradle:8.14.3-jdk21-alpine as gradle-build

WORKDIR /app

COPY build.gradle /app
COPY src /app/src

RUN gradle bootJar --no-daemon --stacktrace
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM eclipse-temurin:21-alpine

ARG DEPENDENCY=/app/build/dependency
COPY --from=gradle-build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=gradle-build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=gradle-build ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java","-cp","app:app/lib/*","com.example.ExampleApplication"]
