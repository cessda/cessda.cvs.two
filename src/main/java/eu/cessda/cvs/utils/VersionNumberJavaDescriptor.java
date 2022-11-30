package eu.cessda.cvs.utils;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class VersionNumberJavaDescriptor extends AbstractTypeDescriptor<VersionNumber> {

    public static final VersionNumberJavaDescriptor INSTANCE = new VersionNumberJavaDescriptor();

    public VersionNumberJavaDescriptor() {
        super(VersionNumber.class, ImmutableMutabilityPlan.INSTANCE);
    }

    @Override
    public VersionNumber fromString(String string) {
        return VersionNumber.fromString(string);
    }

    @Override
    public <X> X unwrap(VersionNumber value, Class<X> type, WrapperOptions options) {
        if (value == null)
            return null;
        if (String.class.isAssignableFrom(type))
            return (X) value.toString();
        throw unknownUnwrap(type);
    }

    @Override
    public <X> VersionNumber wrap(X value, WrapperOptions options) {
        if (value == null)
            return null;
        
        if (String.class.isInstance(value))
            return VersionNumber.fromString(value.toString());
        
        throw unknownWrap(value.getClass());
    }
}
