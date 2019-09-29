FROM adoptopenjdk/openjdk8-openj9:alpine-jre
ENV app_name delay-client.jar
COPY target/dependency /src/app/dependency/
COPY target/${app_name}-*.jar /src/app/$app_name
WORKDIR /src/app
CMD java -jar $app_name