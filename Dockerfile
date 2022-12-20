FROM gradle:5.6.2-jdk11 as java-build
EXPOSE 8763
WORKDIR /opt/pod-isante/auth
COPY build/libs/auth-server-0.0.1-SNAPSHOT.jar auth-server.jar
ENTRYPOINT ["java","-jar","/opt/pod-isante/auth/auth-server.jar"]
#docker pull lahcenezinnour/auth-docker-img:latest
#docker run -p 8763:8763 -t lahcenezinnour/auth-docker-img:latest