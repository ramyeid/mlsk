FROM openjdk:11


RUN apt-get update -y

RUN apt-get install vim -y

RUN apt-get install maven -y

RUN apt-get install python3 -y
RUN apt-get install python3-pip -y

RUN apt-get install npm -y
RUN npm cache clean -f
RUN npm install -g npm@7.19.1
RUN npm install -g n
RUN n stable
RUN npm install -g @angular/cli@12.1.2
RUN npm install --save-dev @angular-devkit/build-angular

RUN chmod -R 777 /usr/local/lib

RUN pip3 install flask
RUN pip3 install pandas
RUN pip3 install statsmodels
RUN pip3 install python-dateutil
RUN pip3 install datetime
RUN pip3 install pytest

RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list
RUN apt-get update && apt-get -y install google-chrome-stable


ENTRYPOINT ["tail", "-f", "/dev/null"]
