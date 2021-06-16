FROM java:8-jdk-alpine
COPY ./out/artifacts/PrecosCombustiveis_jar/PrecosCombustiveis.jar /usr/app/
WORKDIR /usr/app
EXPOSE 8888 8080 8081 8082 8083
ENTRYPOINT ["java", "-jar", "PrecosCombustiveis.jar"]