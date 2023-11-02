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

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class VersionNumberJavaDescriptor extends AbstractTypeDescriptor<VersionNumber> {
    private static final long serialVersionUID = 3118219910023942818L;

    public static final VersionNumberJavaDescriptor INSTANCE = new VersionNumberJavaDescriptor();

    @SuppressWarnings("unchecked") // no typesafe way to obtain cached ImmutableMutabilityPlan instance
    public VersionNumberJavaDescriptor() {
        super(VersionNumber.class, ImmutableMutabilityPlan.INSTANCE);
    }

    @Override
    public VersionNumber fromString(String string) {
        return VersionNumber.fromString(string);
    }

    @SuppressWarnings("unchecked") // verified typesafe
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

        if (value instanceof String)
            return VersionNumber.fromString(value.toString());

        throw unknownWrap(value.getClass());
    }
}
