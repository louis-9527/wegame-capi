FROM alpine:3.18
ENV TZ=Asia/Singapore
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ADD . /opt/wegame-capi
ARG version=8.412.08.1
# Please note that the THIRD-PARTY-LICENSE could be out of date if the base image has been updated recently.
# The Corretto team will update this file but you may see a few days' delay.
RUN wget -O /THIRD-PARTY-LICENSES-20200824.tar.gz https://corretto.aws/downloads/resources/licenses/alpine/THIRD-PARTY-LICENSES-20200824.tar.gz && \
    echo "82f3e50e71b2aee21321b2b33de372feed5befad6ef2196ddec92311bc09becb  /THIRD-PARTY-LICENSES-20200824.tar.gz" | sha256sum -c - && \
    tar x -ovzf THIRD-PARTY-LICENSES-20200824.tar.gz && \
    rm -rf THIRD-PARTY-LICENSES-20200824.tar.gz && \
    wget -O /etc/apk/keys/amazoncorretto.rsa.pub https://apk.corretto.aws/amazoncorretto.rsa.pub && \
    SHA_SUM="6cfdf08be09f32ca298e2d5bd4a359ee2b275765c09b56d514624bf831eafb91" && \
    echo "${SHA_SUM}  /etc/apk/keys/amazoncorretto.rsa.pub" | sha256sum -c - && \
    echo "https://apk.corretto.aws" >> /etc/apk/repositories && \
    apk add --no-cache amazon-corretto-8=$version-r0 && \
    rm -rf /usr/lib/jvm/java-8-amazon-corretto/lib/src.zip

ENV LANG C.UTF-8

ENV JAVA_HOME=/usr/lib/jvm/java-8-amazon-corretto
ENV PATH=$PATH:$JAVA_HOME/bin
ENV CLASSPATH=.:$JAVA_HOME/lib/rt.jar

#install nodejs npm
RUN apk add --no-cache nodejs npm python3 make g++

#install jmx exporter
RUN mkdir -p /opt/jmx-prometheus-javaagent
RUN wget -O /opt/jmx-prometheus-javaagent/jmx_prometheus_javaagent-0.17.0.jar https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.17.0/jmx_prometheus_javaagent-0.17.0.jar

RUN mkdir -p /log/wegame-capi
WORKDIR /opt/wegame-capi
RUN chmod 777 /opt/wegame-capi/target/wegame-capi-1.0.0.jar
WORKDIR /opt/wegame-capi/target
RUN npm install -g aws4
