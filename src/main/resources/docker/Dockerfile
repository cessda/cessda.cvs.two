FROM tomcat:8-alpine

VOLUME /tmp

COPY @project.build.finalName@.war /usr/local/tomcat/webapps/app.war
RUN sh -c 'touch /usr/local/tomcat/webapps/app.war'

ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /usr/local/tomcat/webapps/app.war"]

#RUN sh -c 'echo "exec java -jar ./application.war --spring.datasource.password=\$DATASOURCE_PASSWORD" > ./entrypoint.sh'

#RUN chmod 400 ./application.war && chmod 500 ./entrypoint.sh && chown -R user:user ./
#USER user:user

#EXPOSE 8585
#HEALTHCHECK CMD exit $(echo $(echo $(wget http://localhost:@server.port@@server.contextPath@/health -q -O -) | grep -cv UP))
#ENTRYPOINT ["/bin/sh", "./entrypoint.sh"]

