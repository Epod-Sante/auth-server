package ca.uqtr.authservice.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyPair;
import java.util.Arrays;

import ca.uqtr.authservice.serialisation.FixedSerialVersionUUIDJdbcTokenStore;
import com.zaxxer.hikari.HikariDataSource;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.*;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfigJwt extends AuthorizationServerConfigurerAdapter {

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;
    @Autowired
    DataSource dataSource;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private ClientDetailsService clientDetailsService;
    @Value("${security.oauth2.authorization.key-pair.key-store}")
    private String keyStore;
    @Value("${security.oauth2.authorization.key-pair.alias}")
    private String alias;
    @Value("${security.oauth2.authorization.key-pair.password}")
    private String password;


    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("permitAll()");
    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
       /*clients.
               jdbc(dataSource).
               passwordEncoder(passwordEncoder).
               withClient("SPA").
               authorizedGrantTypes("client_credentials", "password", "refresh_token");*/
         clients.jdbc(dataSource).passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));

        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(jdbcTokenStore());
        tokenServices.setTokenEnhancer(tokenEnhancerChain);
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setAccessTokenValiditySeconds(5000);
        tokenServices.setRefreshTokenValiditySeconds(5000);

        endpoints.tokenServices(tokenServices).
                tokenEnhancer(tokenEnhancerChain).
                userDetailsService(userDetailsService).
                authenticationManager(authenticationManager);
/*
        endpoints.tokenStore(jdbcTokenStore()).
                tokenEnhancer(tokenEnhancerChain)
                .authenticationManager(authenticationManager);*/


    }

    @Bean
    public TokenStore jdbcTokenStore() {
        return new FixedSerialVersionUUIDJdbcTokenStore(dataSource);
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        // symmetric signing key:
        // converter.setSigningKey("123");

        // jks keystore:
        final KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("keystore.jks"), this.password.toCharArray());
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair(this.alias));
        return converter;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }


    @Bean
    public WebResponseExceptionTranslator loggingExceptionTranslator() {
        return new DefaultWebResponseExceptionTranslator() {
            @Override
            public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
                // This is the line that prints the stack trace to the log. You can customise this to format the trace etc if you like
                e.printStackTrace();

                // Carry on handling the exception
                ResponseEntity<OAuth2Exception> responseEntity = super.translate(e);
                HttpHeaders headers = new HttpHeaders();
                headers.setAll(responseEntity.getHeaders().toSingleValueMap());
                OAuth2Exception excBody = responseEntity.getBody();
                return new ResponseEntity<>(excBody, headers, responseEntity.getStatusCode());
            }
        };
    }

//    @Bean
//    @Primary
//    public ResourceServerTokenServices tokenServices() {
//        DefaultTokenServices tokenServices = new DefaultTokenServices();
//        tokenServices.setSupportRefreshToken(true);
//        tokenServices.setAccessTokenValiditySeconds(5000);
//        tokenServices.setTokenStore(jdbcTokenStore());
//        tokenServices.setTokenEnhancer(tokenEnhancer());
//        tokenServices.setClientDetailsService(this.clientDetailsService);
//        tokenServices.setAuthenticationManager(this.authenticationManager);
//        return tokenServices;
//    }

}
