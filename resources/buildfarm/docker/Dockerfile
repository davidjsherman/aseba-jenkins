# Customize the official Jenkins container to allow Pipeline building of Aseba
# David Sherman 2016-11-26

FROM jenkins

USER root

RUN apt-get update && apt-get install -y git cmake gcc g++ qt4-dev-tools libqwt5-qt4 libqwt5-qt4-dev valgrind devscripts build-essential lintian libboost-python-dev

COPY jenkins-plugins.txt /usr/share/jenkins/plugins.txt
RUN /usr/local/bin/plugins.sh /usr/share/jenkins/plugins.txt

USER jenkins
