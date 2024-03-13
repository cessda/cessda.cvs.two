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
package eu.cessda.cvs.service;

import eu.cessda.cvs.security.ActionType;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class IllegalActionTypeException extends Exception
{
    private static final long serialVersionUID = 2825945650155639246L;

    public IllegalActionTypeException()
    {
        super( "Missing action type" );
    }

    public IllegalActionTypeException( ActionType actionType )
    {
        super( String.format( "Action type %s not supported", actionType ));
    }

    public IllegalActionTypeException( ActionType actionType, HttpMethod method )
    {
        super( String.format( "Illegal action type %s for method %s", actionType, method ) );
    }
}
