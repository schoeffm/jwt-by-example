echo "AS_ADMIN_USERPASSWORD=${ADMIN_USERPASSWORD:-test}\nAS_ADMIN_PASSWORD=${ADMIN_PASSWORD:-admin}" > /tmp/pwdfile
$ASADMIN start-domain
$ASADMIN set server-config.java-config.debug-enabled=true
#$ASADMIN set-log-levels --target=server-config org=FINEST
$ASADMIN set configs.config.server-config.admin-service.das-config.dynamic-reload-enabled=true
$ASADMIN set configs.config.server-config.admin-service.das-config.autodeploy-enabled=true
#$ASADMIN disable-phone-home
$ASADMIN stop-domain
