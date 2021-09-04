FROM jenkins/jenkins:lts

ENV JENKINS_HOME /var/jenkins_home

ARG JAVA_OPTS

COPY plugins.txt /usr/share/jenkins/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/plugins.txt
