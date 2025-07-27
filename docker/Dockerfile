FROM amazoncorretto:21

RUN mkdir -p /out
RUN mkdir -p /out/files

COPY target/*.jar /out/app.jar
COPY docker/entrypoint.sh .
RUN (cd /out ; jar -xf app.jar)
RUN mkdir /app
RUN ls /out/
RUN cp -r /out/BOOT-INF/lib /app/lib
RUN cp -r /out/META-INF /app/META-INF
RUN cp -r /out/BOOT-INF/classes/* /app

RUN ls -R /app
RUN chmod +x entrypoint.sh
ENTRYPOINT ./entrypoint.sh
