#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
ARG java_image_tag=8-jdk-focal
FROM eclipse-temurin:${java_image_tag}

ENV DOCKER true
ENV TZ Asia/Shanghai
ENV PAIMON_UI_HOME /opt/paimon-webui

COPY target/paimon-web-server $PAIMON_UI_HOME

RUN useradd paimon && \
    chown -R paimon:paimon $PAIMON_UI_HOME

USER paimon

WORKDIR $PAIMON_UI_HOME

EXPOSE 10088

CMD [ "/bin/bash", "./bin/start.sh"]
