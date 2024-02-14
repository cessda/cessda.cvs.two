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

import org.apache.commons.codec.digest.DigestUtils;

import java.util.function.UnaryOperator;

public enum HashFunction
{
    MD2( "md2", DigestUtils::md2Hex ),
    MD5( "md5", DigestUtils::md5Hex ),
    SHA1( "sha1", DigestUtils::sha1Hex ),
    SHA256( "sha256", DigestUtils::sha256Hex );

    private final String name;
    private final UnaryOperator<String> fn;

    HashFunction( String name, UnaryOperator<String> fn )
    {
        this.name = name;
        this.fn = fn;
    }

    public String getName()
    {
        return this.name;
    }

    public String hash( String string )
    {
        return this.fn.apply( string );
    }

    public static HashFunction fromString( String str )
    {
        for ( HashFunction hf : HashFunction.values() )
        {
            if ( hf.name.equalsIgnoreCase( str ) )
            {
                return hf;
            }
        }
        return null;
    }
}
