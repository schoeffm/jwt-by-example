package de.bender;

import org.eclipse.microprofile.auth.LoginConfig;

import javax.annotation.security.DeclareRoles;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("rest")
@DeclareRoles({"stefan"})
@LoginConfig(authMethod = "MP-JWT")
public class JAXRSConfiguration extends Application {
}
