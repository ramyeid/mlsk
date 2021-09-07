FROM openjdk:11

RUN apt-get update -y
RUN apt-get install libxrender1 libxtst6 libxi6 -y

WORKDIR /apps
COPY mlsk-ui-jar-with-dependencies.jar ./mlsk-ui-jar-with-dependencies.jar

CMD java -jar mlsk-ui-jar-with-dependencies.jar --service-port 6766 --service-host docker.for.mac.host.internal
