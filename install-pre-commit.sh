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

#!/bin/bash
# check if the pre-commit file exists in the .git/hooks/ directory
if [ -f .git/hooks/pre-commit ]; then
  echo "pre-commit hook files is exists"
else
  # write the pre-commit file content to the .git/hooks/pre-commit file
  cat << 'EOF' >> .git/hooks/pre-commit
#!/bin/bash -e
function get_module() {
  local path=$1;
  while true; do
    path=$(dirname $path);
    if [ -f "$path/pom.xml" ]; then
      echo "$path";
      return;
    elif [[ "./" =~ "$path" ]]; then
      return;
    fi
  done
}

modules=();

for file in $(git diff --name-only --cached \*.java); do
  module=$(get_module "$file");
  if [ "" != "$module" ] \
      && [[ ! " ${modules[@]} " =~ " $module " ]]; then
    modules+=("$module");
  fi
done;

if [ ${#modules[@]} -eq 0 ]; then
  exit;
fi

modules_arg=$(printf ",%s" "${modules[@]}");
modules_arg=${modules_arg:1};

export MAVEN_OPTS="-client
  -XX:+TieredCompilation
  -XX:TieredStopAtLevel=1
  -Xverify:none";

echo -e "\033[1;31m Please wait $modules_arg check...\033[0m"
echo -e "\033[1;32m mvn apache-rat:check\033[0m"
mvn -q -pl "$modules_arg" apache-rat:check;
echo -e "\033[1;33m mvn spotless:apply\033[0m"
mvn -q -pl "$modules_arg" spotless:apply;
echo -e "\033[1;34m mvn spotless:check\033[0m"
mvn -q -pl "$modules_arg" spotless:check;
echo -e "\033[1;35m mvn checkstyle:check\033[0m"
mvn -q -pl "$modules_arg" checkstyle:check;

EOF
  # make the .git/hooks/pre-commit file executable
  chmod +x .git/hooks/pre-commit
  echo "pre-commit hook installed successfully"
fi
