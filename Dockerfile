FROM openjdk:8-jre

VOLUME /tmp

RUN addgroup cessda
RUN adduser --ingroup cessda --disabled-password --disabled-login cessda
WORKDIR /cessda.cvmanager

COPY maven/cessda.cvmanager-*.war ./application.war
RUN sh -c 'echo "exec java -jar ./application.war" > ./entrypoint.sh'

RUN chmod 400 ./application.war && chmod 500 ./entrypoint.sh && chown -R cessda:cessda ./
USER cessda:cessda

ENTRYPOINT ["/bin/sh", "./entrypoint.sh"]
