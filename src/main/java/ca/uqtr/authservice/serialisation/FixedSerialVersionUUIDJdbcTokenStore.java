package ca.uqtr.authservice.serialisation;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.ObjectInputStream;

public class FixedSerialVersionUUIDJdbcTokenStore extends JdbcTokenStore {
    public FixedSerialVersionUUIDJdbcTokenStore(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected OAuth2Authentication deserializeAuthentication(byte[] authentication) {
        return deserialize(authentication);
    }

    @Override
    protected OAuth2AccessToken deserializeAccessToken(byte[] token) {
        return deserialize(token);
    }

    @Override
    protected OAuth2RefreshToken deserializeRefreshToken(byte[] token) {
        return deserialize(token);
    }

    @SuppressWarnings("unchecked")
    private <T> T deserialize(byte[] authentication) {
        try {
            return (T) super.deserializeAuthentication(authentication);
        } catch (Exception e) {
            try (ObjectInputStream input = new FixSerialVersionUUID(authentication)) {
                return (T) input.readObject();
            } catch (IOException | ClassNotFoundException e1) {
                throw new IllegalArgumentException(e1);
            }
        }
    }
}
