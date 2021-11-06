FROM openjdk:11

RUN apt-get update -y

RUN apt-get install vim -y

RUN apt-get install maven -y

RUN apt-get install python3 -y
RUN apt-get install python3-pip -y

RUN apt-get install npm -y
RUN npm cache clean -f
RUN npm install -g npm@8.1.0
RUN npm install -g n
RUN n stable
RUN npm install -g @angular/cli@13.0.1
RUN npm install -g @angular-devkit/build-angular

RUN chmod -R 777 /usr/local/lib

RUN pip3 install flask
RUN pip3 install pandas
RUN pip3 install statsmodels
RUN pip3 install sklearn
RUN pip3 install python-dateutil
RUN pip3 install datetime
RUN pip3 install pytest
RUN pip3 install pytest-cov

ENTRYPOINT ["tail", "-f", "/dev/null"]
