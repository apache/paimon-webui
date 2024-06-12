# Quick Start

This short guide will show you how to deploy and use Paimon Web.

## Prerequisite

- Install Java 8 runtime environment.
- Apache Flink-1.17+
- Apache Paimon-0.8+

## Download binary package

Download the Paimon Web installation package from paimon.apache.org and proceed with the following steps.

```shell
# Download the binary installation package of Paimon Web
[root@paimon ~]# wget https://repository.apache.org/snapshots/org/apache/paimon/paimon-web/0.1-SNAPSHOT/apache-paimon-webui-0.1-SNAPSHOT-bin.tar.gz

# Extract the installation package
[root@paimon ~]# tar zxf apache-paimon-webui-0.1-SNAPSHOT-bin.tar.gz
    
# Rename the directory to paimon-webui for simplicity
[root@paimon ~]# mv apache-paimon-webui-0.1-SNAPSHOT-bin.tar.gz paimon-webui
```
The directory after decompression is as follows:
```shell
bin
config
libs
scripts
ui
```

## Create a database and execute the sql script

You need to create a database named paimon and execute the paimon-mysql.sql file in the scripts directory.

## Modify the configuration file

Modify the port and other configuration information in the application.yml file in the config directory if necessary,
and then configure the database connection information in the application-prod.yml file.

## Configuring env

You need to configure FLINK_HOME, ACTION_JAR_PATH, JAVA_HOME and other information in the env.sh script in the bin directory. 
If you do not use the CDC function, FLINK_HOME and ACTION_JAR_PATH do not need to be configured.

```shell
[root@paimon ~]# cd bin
[root@paimon ~]# chmod 755 env.sh
[root@paimon ~]# vim evn.sh
```

## Start the service

```shell
[root@paimon ~]# bin/start.sh
```
Then enter `http://{ip}:{port}` in the browser to enter the paimon web page.

## Execute a Flink SQL

- Start Flink SQL Gateway

```shell
[root@paimon ~]#  ./bin/sql-gateway.sh start -Dsql-gateway.endpoint.rest.address=localhost
```

- Create a cluster instance in the cluster management of paimon web

![img_1.png](../../public/img/img_1.png)

- Execute a Flink SQL on the SQL IDE page

![img_2.png](../../public/img/img_2.png)