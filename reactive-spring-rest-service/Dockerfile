FROM oracle/graalvm-ce:19.2.0.1
ENV app_name reactive-spring-rest-service
#COPY target/dependency /src/app/dependency/
COPY target/${app_name}-*.jar /src/app/${app_name}.jar
WORKDIR /src/app
CMD java -jar ${app_name}.jar