#!/bin/sh

#变量定义
source_path=/home/centos/project/source
deploy_path=/home/centos/project/deploy
deploy_env=test
app_name=wegame-capi
app_version=1.0.0
app_branch=develop

rm -rf $source_path
rm -rf $deploy_path

if [ ! -d $source_path ]; then
  mkdir -p $source_path
fi
if [ ! -d $$deploy_path ]; then
  mkdir -p $deploy_path
fi
if [ ! -d $source_path/$app_name ]; then
  cd $source_path
  git config --global credential.helper store
  git clone https://github.com/Liv-Sports/$app_name.git
fi

#拉取最新代码
cd $source_path/$app_name
git checkout $app_branch
git pull

#maven打包
rm -rf /opt/maven/repo/com/wegame/*
mvn clean package -P$deploy_env -Dmaven.test.skip=true

cp $source_path/$app_name/target/$app_name-$app_version.jar $deploy_path
cd $deploy_path

#服务启动
port=`ps -ef | grep $deploy_path/$app_name-$app_version.jar | grep -v "grep" | awk '{print $2}'`
echo $port
for id in $port
do
    kill -9 $id
    echo "killed $id"
done

nohup java -jar $deploy_path/$app_name-$app_version.jar > $deploy_path/nohup.out 2>&1 &
tail -f $deploy_path/nohup.out