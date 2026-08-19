#
# Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# This Dockerfile must be run after both the Java and Angular
# components of the application have been compiled
FROM eclipse-temurin:11 AS builder
WORKDIR /cvs

COPY target/cvs*.jar cvs.jar

RUN java -Djarmode=layertools -jar cvs.jar extract

FROM eclipse-temurin:11-jre AS final
WORKDIR /opt/cessda/cvs/

# Container Information
LABEL maintainer='CESSDA-ERIC "support@cessda.eu"'

# Copy exploded JAR
COPY --from=builder /cvs/dependencies/ ./
COPY --from=builder /cvs/spring-boot-loader/ ./
COPY --from=builder /cvs/snapshot-dependencies/ ./
COPY --from=builder /cvs/application/ ./

# Entrypoint - Start CVS
USER 1000
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
