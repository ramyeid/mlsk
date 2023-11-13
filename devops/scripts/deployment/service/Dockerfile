FROM ramyeid/mlsk-base:latest

EXPOSE 6766

RUN apt-get update -y
RUN apt-get install vim -y

RUN apt-get install maven -y

RUN apt-get install python3 -y
RUN apt-get install python3-pip -y

RUN chmod -R 777 /usr/local/lib

RUN pip3 install --upgrade pip

COPY mlsk-service-impl.jar /mlsk-service-impl.jar
COPY engine /engine

RUN pip3 install -r /engine/requirements.txt

ENTRYPOINT ["java", \
            "-Dserver.port=6766", \
            "-jar","/mlsk-service-impl.jar", \
            "--engine-ports", "6767,6768,6769", \
            "--engine-path", "/engine", \
            "--logs-path", "/mlsk-logs", \
            "--log-level", "INFO" \
            "--engine-log-level", "INFO"]
