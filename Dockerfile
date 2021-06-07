# syntax=docker/dockerfile:1
FROM openjdk:11
RUN apt-get update
ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get install -y apt-utils && \
    apt-get install -y wget

RUN export LANGUAGE=en_US.UTF-8
RUN export LANG=en_US.UTF-8
RUN export LC_ALL=en_US.UTF-8
RUN export LANGUAGE=es_MX.UTF-8
RUN export LANG=es_MX.UTF-8
RUN export LC_ALL=es_MX.UTF-8

RUN apt-get install -y locales && \
    locale-gen en_US.UTF-8 && \
    dpkg-reconfigure locales

RUN apt-get install -y mariadb-server && \
    apt-get -y upgrade && \
    apt-get -y update

RUN mkdir /home/sofia
WORKDIR /home/sofia

RUN wget https://github.com/Cabezudo/sofia-web-server/raw/master/sofia-web-server-0.1-all.zip && \
    apt-get install -y unzip && \
    unzip sofia-web-server-0.1-all.zip && \
    rm -rf /var/lib/apt/lists/* && \
    rm /home/sofia/sofia-web-server-0.1-all.zip

    RUN chmod 744 /home/sofia/server/run.sh

RUN echo \
    "environment=development\n" \
    "database.driver=com.mysql.cj.jdbc.Driver\n" \
    "database.hostname=localhost\n" \
    "database.port=3306\n" \
    "database.name=sofia\n" \
    "database.username=root\n" \
    "database.password=password\n" \
    "server.port=8080\n" \
    "system.home=/home/sofia/server\n" > /home/sofia/server/sofia.configuration.properties

WORKDIR /home/sofia/server

CMD service mysql start && \
    /usr/bin/mysql -u root -e "SET PASSWORD FOR root@localhost = PASSWORD('password')" && \
    /usr/bin/mysql -u root -e "FLUSH PRIVILEGES" && \

    ./run.sh -i
