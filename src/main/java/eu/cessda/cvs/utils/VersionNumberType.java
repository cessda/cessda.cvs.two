/*
 * Copyright © 2017-2023 CESSDA ERIC (support@cessda.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
