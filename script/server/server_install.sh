#!/bin/sh
# 1.安装jdk -- 判断有没有jdk已经安装
sudo yum -y install java-1.8.0-openjdk.x86_64

# 2.安装maven
sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
# 2.1 生成缓存：
sudo yum clean all
sudo yum makecache
# 2.2 安装
sudo yum -y install apache-maven

sudo chown -R centos:centos /opt

if [ -d /op/maven/repo ]; then
  mkdir -p /opt/maven/repo
fi

# 2.3 写入maven配置
cat >/etc/maven/settings.xml<<EOF
<?xml version="1.0" encoding="UTF-8"?>

<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd">
  <localRepository>/opt/maven/repo</localRepository>

  <pluginGroups>
  </pluginGroups>

  <proxies>
  </proxies>
  <servers>
    <server>
      <id>maven-public</id>
      <username>admin</username>
      <password>qwe123!@#</password>
    </server>

    <server>
      <id>maven-releases</id>
      <username>admin</username>
      <password>qwe123!@#</password>
    </server>

    <server>
      <id>maven-snapshots</id>
      <username>admin</username>
      <password>qwe123!@#</password>
    </server>

  </servers>

  <mirrors>

    <mirror>
      <id>maven-public</id>
      <name>maven-public</name>
      <url>http://spherelinkgrid.click:8081/repository/maven-public/</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
  </mirrors>

  <profiles>

  </profiles>

</settings>
EOF


# 安装git
sudo yum -y install git
#git config --global --unset user.name
#git config --global --unset user.email
#git config --global --unset credential.helper
#git config --global user.name "xiaoheilong1616@gmail.com"
#git config --global user.email "xiaoheilong1616@gmail.com"
git config --global credential.helper store