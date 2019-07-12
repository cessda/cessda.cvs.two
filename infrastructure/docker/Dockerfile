# OpenJDK Base Container
FROM openjdk:8-jre

# Container Information
LABEL MAINTAINER="CESSDA-ERIC 'support@cessda.eu'"

# Install Tomcat Native
RUN apt-get -qq update && apt-get -qq upgrade
RUN apt-get install -qq -y libtcnative-1

# Create Volume tmp and add JAR artifacts
VOLUME /tmp
RUN addgroup cessda
RUN adduser --ingroup cessda --disabled-password --disabled-login cessda
WORKDIR /@project.artifactId@

COPY maven/@project.build.finalName@.war ./application.war
RUN sh -c 'echo "exec java -jar ./application.war" > ./entrypoint.sh'

RUN chmod 400 ./application.war && chmod 500 ./entrypoint.sh && chown -R cessda:cessda ./
USER cessda:cessda

ENTRYPOINT ["/bin/sh", "./entrypoint.sh"]
