FROM liquibase/liquibase:4.2.2
COPY ./liquibase-changelog.tar /liquibase
RUN tar -xvf liquibase-changelog.tar \
    && rm -rf ./liquibase-changelog.tar
COPY --chmod=755 ./bin/entrypoint.sh ./entrypoint.sh
CMD ["/bin/bash","-c","./entrypoint.sh"]
