# This Dockerfile must be run after both the Java and Angular
# components of the application have been compiled
FROM openjdk:11

# Container Information
LABEL maintainer='CESSDA-ERIC "support@cessda.eu"'

# User configuration
RUN addgroup --system --gid=1000 cvs && adduser --system --uid=1000 --ingroup=cvs cvs

# Copy JAR artifacts
WORKDIR /opt/cessda/vocabularies-service
COPY target/cvs*/WEB-INF/lib /opt/cessda/vocabularies-service/lib
COPY target/cvs*/META-INF /opt/cessda/vocabularies-service/META-INF
COPY target/cvs*/WEB-INF/classes /opt/cessda/vocabularies-service
COPY target/cvs*/WEB-INF/web.xml /opt/cessda/vocabularies-service/WEB-INF

# Entrypoint - Start CVS
USER cvs
ENTRYPOINT ["java", "-cp", ".:lib/*", "eu.cessda.cvs.CvsApp"]
