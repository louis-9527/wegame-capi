FROM alpine:3.18

ADD . /opt/wegame-capi
ARG version=8.412.08.1

# Install system dependencies and Java
RUN wget -O /THIRD-PARTY-LICENSES-20200824.tar.gz https://corretto.aws/downloads/resources/licenses/alpine/THIRD-PARTY-LICENSES-20200824.tar.gz && \
    echo "82f3e50e71b2aee21321b2b33de372feed5befad6ef2196ddec92311bc09becb  /THIRD-PARTY-LICENSES-20200824.tar.gz" | sha256sum -c - && \
    tar x -ovzf THIRD-PARTY-LICENSES-20200824.tar.gz && \
    rm -rf THIRD-PARTY-LICENSES-20200824.tar.gz && \
    wget -O /etc/apk/keys/amazoncorretto.rsa.pub https://apk.corretto.aws/amazoncorretto.rsa.pub && \
    SHA_SUM="6cfdf08be09f32ca298e2d5bd4a359ee2b275765c09b56d514624bf831eafb91" && \
    echo "${SHA_SUM}  /etc/apk/keys/amazoncorretto.rsa.pub" | sha256sum -c - && \
    echo "https://apk.corretto.aws" >> /etc/apk/repositories && \
    apk add --no-cache amazon-corretto-8=${version}-r0 && \
    rm -rf /usr/lib/jvm/java-8-amazon-corretto/lib/src.zip

# Set timezone
RUN apk add --no-cache tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Kuala_Lumpur /etc/localtime && \
    echo "Asia/Kuala_Lumpur" > /etc/timezone

ENV LANG C.UTF-8
ENV JAVA_HOME=/usr/lib/jvm/java-8-amazon-corretto
ENV PATH=$PATH:$JAVA_HOME/bin
ENV CLASSPATH=.:$JAVA_HOME/lib/rt.jar

# Install Node.js, npm, python3, make, g++
RUN apk add --no-cache nodejs npm python3 make g++

# Install JMX exporter
RUN mkdir -p /opt/jmx-prometheus-javaagent && \
    wget -O /opt/jmx-prometheus-javaagent/jmx_prometheus_javaagent-0.17.0.jar https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.17.0/jmx_prometheus_javaagent-0.17.0.jar

# Download and extract ARMS agent
WORKDIR /root/
RUN wget "http://arms-apm-ap-southeast-1.oss-ap-southeast-1.aliyuncs.com/AliyunJavaAgent.zip" -O AliyunJavaAgent.zip && \
    unzip AliyunJavaAgent.zip -d /root/

# Set ARMS environment variables
ENV arms_licenseKey=1hlahoc4rpc@f47c6d65f95452e
ENV arms_appName=MariaMakilling-qat-wegame-capi
ENV JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS} -javaagent:/root/AliyunJavaAgent/aliyun-java-agent.jar -Darms.licenseKey=${arms_licenseKey} -Darms.appName=${arms_appName}"

# Logging directory and application setup
RUN mkdir -p /log/wegame-capi && \
    chmod 777 /opt/wegame-capi/target/wegame-capi-1.0.0.jar

# Install aws4 globally for your app
RUN npm install -g aws4

WORKDIR /opt/wegame-capi/target
