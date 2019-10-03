# OpenJDK Base Container
FROM tomcat:8.5-jdk8

# Container Information
LABEL MAINTAINER="CESSDA-ERIC 'support@cessda.eu'"

# Create CESSDA User
RUN useradd cessda

# Add WAR artifact
WORKDIR /@project.artifactId@
COPY maven/@project.build.finalName@.war ./@project.artifactId@.war

# Switch to CESSDA User and set entrypoint
USER cessda
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=50.0", "-jar", "./@project.artifactId@.war"]
