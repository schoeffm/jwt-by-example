package de.bender;

import javax.enterprise.context.ApplicationScoped;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singletonList;

/**
 * Using JWT there'll be a pre-populated {@link javax.ws.rs.core.SecurityContext} ready for you to be used - but
 * in case you'd like to extend the information (especially the groups/roles of a requesting user) delivered by
 * the JWT you can use an {@link IdentityStore}-implementation (with a proper
 * {@link javax.security.enterprise.identitystore.IdentityStore.ValidationType} and scoped as
 * {@link ApplicationScoped})
 */
@ApplicationScoped
public class CustomIdentityStore implements IdentityStore {

    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
        System.out.printf("Find roles for given user '%s' in DB\n", validationResult.getCallerPrincipal().getName());

        return new HashSet<>(singletonList("yosh"));
    }

    @Override
    public Set<ValidationType> validationTypes() {
        return new HashSet<>(singletonList(ValidationType.PROVIDE_GROUPS));
    }
}
