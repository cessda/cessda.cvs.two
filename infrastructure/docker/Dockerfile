# OpenJDK Base Container
FROM tomcat:8.5-jdk8

# Container Information
LABEL MAINTAINER="CESSDA-ERIC 'support@cessda.eu'"

# Remove base Tomacat webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Add WAR artifact
COPY maven/@project.build.finalName@.war /usr/local/tomcat/webapps/ROOT.war

# Switch to CESSDA User and set entrypoint
RUN useradd cessda
RUN chown -R cessda /usr/local/tomcat
USER cessda
