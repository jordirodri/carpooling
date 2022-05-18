FROM amazoncorretto:11-alpine-jdk

EXPOSE 9091

COPY target/carpooling-1.0.0.jar ./
 
ENTRYPOINT [ "java","-jar","carpooling-1.0.0.jar" ]
