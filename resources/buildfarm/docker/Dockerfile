# Customize the official Jenkins container to allow Pipeline building of Aseba
# David Sherman 2016-11-26

FROM jenkins

USER root

RUN apt-get update && apt-get install -y sudo git cmake gcc g++ qt4-dev-tools libqwt5-qt4 libqwt5-qt4-dev valgrind devscripts build-essential lintian libboost-python-dev libudev-dev libxml2-dev
RUN gpasswd -a jenkins sudo && \
    sed -i.bkp -e \
      's/%sudo\s\+ALL=(ALL\(:ALL\)\?)\s\+ALL/%sudo ALL=NOPASSWD:ALL/g' \
      /etc/sudoers

COPY jenkins-plugins.txt /usr/share/jenkins/plugins.txt
RUN /usr/local/bin/install-plugins.sh $(cat /usr/share/jenkins/plugins.txt)

USER jenkins
