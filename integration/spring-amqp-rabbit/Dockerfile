FROM ubuntu:22.04

ENV JAVA_HOME /opt/jdk
ENV PATH $JAVA_HOME/bin:$PATH

ADD "https://cdn.azul.com/zulu/bin/zulu17.44.17-ca-crac-jdk17.0.8-linux_x64.tar.gz" $JAVA_HOME/openjdk.tar.gz
RUN tar --extract --file $JAVA_HOME/openjdk.tar.gz --directory "$JAVA_HOME" --strip-components 1; rm $JAVA_HOME/openjdk.tar.gz;
RUN mkdir -p /opt/app
COPY build/libs/spring-amqp-rabbit.jar /opt/app/spring-amqp-rabbit.jar
RUN mkdir -p /opt/crac-files

