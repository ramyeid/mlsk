FROM ramyeid/mlsk-base:latest

RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list
RUN apt-get update && apt-get -y install google-chrome-stable


RUN curl -s -L https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-4.6.2.2472-linux.zip -o sonarscanner.zip
RUN unzip -qq sonarscanner.zip
RUN rm -rf sonarscanner.zip
RUN mv sonar-scanner-4.6.2.2472-linux sonar-scanner

ENV SONAR_RUNNER_HOME=sonar-scanner
ENV PATH="/sonar-scanner/bin:${PATH}"


ENTRYPOINT ["tail", "-f", "/dev/null"]
