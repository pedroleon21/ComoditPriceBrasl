FROM java:8-jdk-alpine
COPY ./build/libs/precoscombustiveisapi-shadow-1.0.jar /usr/app/
WORKDIR /usr/app
EXPOSE 8888 8080 8081 8082 8083
ENTRYPOINT ["java", "-jar", "precosombustiveisapi-shadow-1.0.jar"]