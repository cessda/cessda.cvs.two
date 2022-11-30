package eu.cessda.cvs.utils;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class VersionNumberType extends AbstractSingleColumnStandardBasicType<VersionNumber> implements LiteralType<VersionNumber> {

    public static final VersionNumberType INSTANCE = new VersionNumberType();

    public VersionNumberType() {
        super(VarcharTypeDescriptor.INSTANCE, VersionNumberJavaDescriptor.INSTANCE);
    }

    @Override
    public String toString(VersionNumber value) throws HibernateException {
        return value.toString();
    }

    @Override
    public VersionNumber fromString(String string) {
        return VersionNumber.fromString(string);
    }

    @Override
    public String getName() {
        return "VersionNumber";
    }

    @Override
	public String objectToSQLString(VersionNumber value, Dialect dialect) throws Exception {
		return "{d '" + value.toString() + "'}";
	}
}
