FROM eclipse-temurin:11-jre-focal
COPY *.jar feature-flag-api-service.jar
COPY --chmod=777 ./resources/feature_flags.json ./feature_flags.json
#EXPOSE 8071
ENV JAVA_OPTS="-Dorg.apache.el.parser.SKIP_IDENTIFIER_CHECK=true"
CMD ["java","-jar","feature-flag-api-service.jar","${JAVA_OPTS}"]
#ENTRYPOINT ["./entrypoint.sh"]
