package de.aldist.authrorizationserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class OAuth2JwtAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

  private final AuthenticationManager authenticationManager;
  private final BCryptPasswordEncoder passwordEncoder;

  @Value("${secret.jwt.access-token-key}")
  private String jwtSigningKey;
  @Value("${secret.jwt.access-token-validity-sec}")
  private int jwtAccessTokenValidity;
  @Value("${secret.jwt.refresh-token-validity-sec}")
  private int jwtRefreshTokenValidity;

  @Value("${client.id1}")
  private String clientId1;
  @Value("${client.secret1}")
  private String clientSecret1;
  @Value("${client.grant-types1}")
  private String[] clientGrantTypes1;
  @Value("${client.scopes1}")
  private String[] clientScopes1;

  @Autowired
  public OAuth2JwtAuthorizationServerConfig(
      @Qualifier("authenticationManagerBean") AuthenticationManager authenticationManager,
      BCryptPasswordEncoder passwordEncoder) {
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
  }

  // TODO: proper exception handling
  @Override
  public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
    //oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    oauthServer.tokenKeyAccess("hasAuthority('ROLE_TRUSTED_CLIENT')")
        .checkTokenAccess("isAuthenticated()");
  }

  // TODO: proper exception handling
  @Override
  public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    endpoints
        .tokenStore(this.tokenStore())
        .accessTokenConverter(this.accessTokenConverter())
        .authenticationManager(this.authenticationManager);
  }

  @Override
  public void configure(final ClientDetailsServiceConfigurer clients)
      throws Exception {
    clients.inMemory()
        .withClient(this.clientId1)
        .secret(this.passwordEncoder.encode(this.clientSecret1))
        .authorizedGrantTypes(this.clientGrantTypes1)
        .scopes(this.clientScopes1)
        .accessTokenValiditySeconds(this.jwtAccessTokenValidity)
        .refreshTokenValiditySeconds(this.jwtRefreshTokenValidity);
  }

  @Bean
  public JwtAccessTokenConverter accessTokenConverter() {
    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    converter.setSigningKey(this.jwtSigningKey);

    return converter;
  }

  @Bean
  public TokenStore tokenStore() {
    return new JwtTokenStore(this.accessTokenConverter());
  }

  @Bean
  @Primary
  public DefaultTokenServices tokenServices() {
    final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
    defaultTokenServices.setTokenStore(this.tokenStore());
    defaultTokenServices.setSupportRefreshToken(true);

    return defaultTokenServices;
  }
}
