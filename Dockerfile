# Use OpenJDK 8 base image (Debian-based, not Alpine)
FROM openjdk:8-jdk-slim
# Set working directory
WORKDIR /opt/wegame-capi
# Copy application files
ADD . /opt/wegame-capi
# Set environment variables
ENV LANG C.UTF-8
ENV JAVA_HOME=/usr/local/openjdk-8
ENV PATH=$PATH:$JAVA_HOME/bin
ENV CLASSPATH=.:$JAVA_HOME/lib
# Install additional tools: nodejs, npm, python3, make, g++
# (Debian/Ubuntu package names are slightly different than Alpine)
RUN apt-get update && apt-get install -y \
    wget \
    procps \
    unzip \
    nodejs \
    npm \
    python3 \
    make \
    g++ \
    tzdata \
 && rm -rf /var/lib/apt/lists/*
# Set timezone
RUN ln -sf /usr/share/zoneinfo/Asia/Kuala_Lumpur /etc/localtime && \
    echo "Asia/Kuala_Lumpur" > /etc/timezone
# Install aws4 globally using npm
RUN npm install -g aws4
# Install jmx exporter agent
RUN mkdir -p /opt/jmx-prometheus-javaagent && \
    wget -O /opt/jmx-prometheus-javaagent/jmx_prometheus_javaagent-0.17.0.jar https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.17.0/jmx_prometheus_javaagent-0.17.0.jar
# Prepare log directory
RUN mkdir -p /log/wegame-capi
# Make sure your JAR file is executable
RUN chmod 777 /opt/wegame-capi/target/wegame-capi-1.0.0.jar
# Download and extract ARMS agent
WORKDIR /root/
RUN wget "http://arms-apm-ap-southeast-1.oss-ap-southeast-1.aliyuncs.com/AliyunJavaAgent.zip" -O AliyunJavaAgent.zip && \
    unzip AliyunJavaAgent.zip -d /root/
# Set ARMS environment variables
#ENV arms_licenseKey=1hlahoc4rpc@f47c6d65f95452e
#ENV arms_appName=MariaMakilling-qat-wegame-capi
#ENV JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS} -javaagent:/root/AliyunJavaAgent/aliyun-java-agent.jar -Darms.licenseKey=${arms_licenseKey} -Darms.appName=${arms_appName}"
# Final working directory
WORKDIR /opt/wegame-capi/target