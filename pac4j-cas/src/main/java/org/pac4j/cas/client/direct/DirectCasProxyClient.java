package org.pac4j.cas.client.direct;

import org.pac4j.cas.authorization.DefaultCasAuthorizationGenerator;
import org.pac4j.cas.client.CasProxyReceptor;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.cas.credentials.authenticator.CasAuthenticator;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.extractor.ParameterExtractor;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;

/**
 * <p>This class is the direct client to authenticate users based on CAS proxy tickets.</p>
 *
 * <p>The configuration can be defined via the {@link #configuration} object.</p>
 *
 * <p>As no session is meant to be created, this client does not handle CAS logout requests.</p>
 *
 * <p>For proxy support, a {@link CasProxyReceptor} must be defined in the configuration (the corresponding "callback filter" must be enabled)
 * and set to the CAS configuration of this client. In that case, a {@link org.pac4j.cas.profile.CasProxyProfile} will be return
 * (instead of a {@link org.pac4j.cas.profile.CasProfile}) to be able to request proxy tickets.</p>
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class DirectCasProxyClient extends DirectClient<TokenCredentials, CommonProfile> {

    private CasConfiguration configuration;

    private String serviceUrl;

    public DirectCasProxyClient() { }

    public DirectCasProxyClient(final CasConfiguration casConfiguration, final String serviceUrl) {
        this.configuration = casConfiguration;
        this.serviceUrl = serviceUrl;
    }

    @Override
    protected void clientInit(final WebContext context) {
        CommonHelper.assertNotNull("configuration", this.configuration);
        CommonHelper.assertNotBlank("serviceUrl", this.serviceUrl);
        // must be a CAS proxy protocol
        final CasProtocol protocol = configuration.getProtocol();
        CommonHelper.assertTrue(protocol == CasProtocol.CAS20_PROXY || protocol == CasProtocol.CAS30_PROXY, "The DirectCasProxyClient must be configured with a CAS proxy protocol (CAS20_PROXY or CAS30_PROXY)");
        configuration.init(context);

        defaultCredentialsExtractor(new ParameterExtractor(CasConfiguration.TICKET_PARAMETER, true, false, getName()));
        defaultAuthenticator(new CasAuthenticator(configuration, this.serviceUrl));
        addAuthorizationGenerator(new DefaultCasAuthorizationGenerator<>());
    }

    public CasConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(final CasConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(final String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "configuration", this.configuration, "serviceUrl", serviceUrl);
    }
}
