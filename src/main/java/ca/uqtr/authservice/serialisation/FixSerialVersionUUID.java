package ca.uqtr.authservice.serialisation;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class FixSerialVersionUUID extends ObjectInputStream {

    public FixSerialVersionUUID(byte[] bytes) throws IOException {
        super(new ByteArrayInputStream(bytes));
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        ObjectStreamClass resultClassDescriptor = super.readClassDescriptor();

        if (resultClassDescriptor.getName().equals(GrantedAuthority.class.getName())) {
            ObjectStreamClass mostRecentSerialVersionUUID = ObjectStreamClass.lookup(GrantedAuthority.class);
            return mostRecentSerialVersionUUID;
        }

        return resultClassDescriptor;
    }
}