/*
 *   @(#) $Id$
 *
 *   Copyright 2004 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.apache.ldap.server.configuration;

import java.io.File;
import java.util.Set;

import org.apache.ldap.server.interceptor.InterceptorChain;
import org.apache.mina.registry.ServiceRegistry;

/**
 * A {@link Configuration} that starts up ApacheDS.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class StartupConfiguration extends Configuration
{
    private static final long serialVersionUID = 4826762196566871677L;

    protected File homeDirectory;
    protected File workingDirectory;
    protected boolean enableAnonymousAccess;
    protected Set authenticators; // Set<Authenticator> and their properties>?
    protected InterceptorChain interceptors; // and their properties?
    protected ServiceRegistry minaServiceRegistry;
    protected int ldapPort = 389;
    protected int ldapsPort = 636;
    protected boolean enableKerberos;
    
    protected Set bootstrapSchemas; // Set<BootstrapSchema>
    protected Set contextParitions; // Set<suffix, partition, indices, attributes, and properties>?
    protected Set testEntries; // Set<Attributes?>
    
    protected StartupConfiguration()
    {
    }
}
