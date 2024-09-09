#!/usr/bin/env bash

#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

##
## Required variables
##
RELEASE_VERSION=${RELEASE_VERSION}

if [ -z "${RELEASE_VERSION}" ]; then
	echo "RELEASE_VERSION is unset"
	exit 1
fi

# fail immediately
set -o errexit
set -o nounset

CURR_DIR=`pwd`
BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
PROJECT_ROOT="$( cd "$( dirname "${BASE_DIR}/../../../" )" >/dev/null && pwd )"

if [ "$(uname)" == "Darwin" ]; then
    SHASUM="shasum -a 512"
else
    SHASUM="sha512sum"
fi

###########################

RELEASE_DIR=${PROJECT_ROOT}/release
DIST_TARGET_DIR=${PROJECT_ROOT}/paimon-web-dist/target

echo "Creating binary package"

cd ${DIST_TARGET_DIR}
tar xzvf apache-paimon-webui-${RELEASE_VERSION}-bin.tar.gz
tar czf ${RELEASE_DIR}/apache-paimon-webui-${RELEASE_VERSION}-bin.tgz apache-paimon-webui-${RELEASE_VERSION}-bin/*
gpg --armor --detach-sig ${RELEASE_DIR}/apache-paimon-webui-${RELEASE_VERSION}-bin.tgz
cd ${RELEASE_DIR}

gpg --armor --detach-sig ${RELEASE_DIR}/apache-paimon-webui-${RELEASE_VERSION}-bin.tgz
cd ${RELEASE_DIR}
${SHASUM} apache-paimon-webui-${RELEASE_VERSION}-bin.tgz > apache-paimon-webui-${RELEASE_VERSION}-bin.tgz.sha512

rm -rf ${DIST_TARGET_DIR}/apache-paimon-webui-${RELEASE_VERSION}-bin

echo "Done. Binary release package and signatures created under ${RELEASE_DIR}/."

cd ${CURR_DIR}
