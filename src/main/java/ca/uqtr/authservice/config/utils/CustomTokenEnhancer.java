package ca.uqtr.authservice.config.utils;

import ca.uqtr.authservice.entity.Account;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class CustomTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        final Map<String, Object> additionalInfo = new HashMap<>();
        /*additionalInfo.put("organization", authentication.getName() + randomAlphabetic(4));
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);*/
        additionalInfo.put("organization", "POD iSante" );
        additionalInfo.put("id", account.getId() );
        additionalInfo.put("role", account.getUser().getRole().getName() );
        additionalInfo.put("institution_code", account.getUser().getInstitution().getInstitutionCode() );
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
