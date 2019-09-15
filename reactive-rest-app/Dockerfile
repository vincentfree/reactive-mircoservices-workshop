FROM adoptopenjdk/openjdk8-openj9:alpine-jre
ENV app_name reactive-rest-app.jar
COPY target/dependency /src/app/dependency/
COPY target/reactive-rest-app-*.jar /src/app/$app_name
WORKDIR /src/app
CMD java -jar $app_name