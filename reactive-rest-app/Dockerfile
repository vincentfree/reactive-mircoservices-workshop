FROM adoptopenjdk/openjdk8-openj9:alpine-jre
ENV app_name reactive-rest-app
COPY target/dependency /src/app/dependency/
COPY target/${app_name}-*.jar /src/app/${app_name}.jar
WORKDIR /src/app
CMD java -jar ${app_name}.jar