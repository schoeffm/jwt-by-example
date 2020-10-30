FROM payara/server-full:5.201 as base-payara

ENV PAYARA_PATH=$HOME_DIR
ENV ASADMIN $PAYARA_DIR/bin/asadmin --user $ADMIN_USER --passwordfile=/tmp/pwdfile
ENV JAVA_HOME /usr/lib/jvm/zulu-8-amd64
ARG asadmin_file=${asadmin_file:-asadmin.sh}

COPY ${asadmin_file} $CONFIG_DIR/asadmin.sh
RUN ${CONFIG_DIR}/asadmin.sh

COPY target/*.war $DEPLOY_DIR
