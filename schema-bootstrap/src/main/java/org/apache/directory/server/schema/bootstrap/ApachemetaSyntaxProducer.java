/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.server.schema.bootstrap;


import javax.naming.NamingException;

import org.apache.directory.server.constants.MetaSchemaConstants;
import org.apache.directory.server.schema.registries.Registries;
import org.apache.directory.shared.ldap.NotImplementedException;
import org.apache.directory.shared.ldap.schema.LdapSyntax;
import org.apache.directory.shared.ldap.schema.SyntaxChecker;
import org.apache.directory.shared.ldap.schema.syntaxChecker.NumericOidSyntaxChecker;
import org.apache.directory.shared.ldap.schema.syntaxChecker.NumericStringSyntaxChecker;
import org.apache.directory.shared.ldap.schema.syntaxChecker.ObjectClassTypeSyntaxChecker;
import org.apache.directory.shared.ldap.schema.syntaxChecker.ObjectNameSyntaxChecker;
import org.apache.directory.shared.ldap.schema.syntaxChecker.OidSyntaxChecker;


/**
 * A producer of Syntax objects for the apachemeta schema.
 * Modified by hand from generated code
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ApachemetaSyntaxProducer extends AbstractBootstrapProducer
{
    public ApachemetaSyntaxProducer()
    {
        super( ProducerTypeEnum.SYNTAX_PRODUCER );
    }


    // ------------------------------------------------------------------------
    // BootstrapProducer Methods
    // ------------------------------------------------------------------------


    /**
     * @see BootstrapProducer#produce(DefaultRegistries, ProducerCallback)
     */
    public void produce( Registries registries, ProducerCallback cb )
        throws NamingException
    {
        LdapSyntax syntax = null;
        
        syntax = new NameOrNumericIdSyntax();
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new NumericOidSyntax();
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new ObjectClassTypeSyntax();
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new NumberSyntax();
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );

        syntax = new ObjectNameSyntax();
        cb.schemaObjectProduced( this, syntax.getOid(), syntax );
    }
    
    
    public static class NumericOidSyntax implements LdapSyntax
    {
        private static final long serialVersionUID = 1L;
        private final static String OID = "1.3.6.1.4.1.18060.0.4.0.0.2";
        private final static SyntaxChecker CHECKER = new OidSyntaxChecker();
        private final static String[] NAMES = new String[] { "numericOid" };
        
        public final SyntaxChecker getSyntaxChecker() throws NamingException
        {
            return CHECKER;
        }

        public final boolean isHumanReadable()
        {
            return true;
        }

        public final String getDescription()
        {
            return "The syntax for numericoids.";
        }

        public final String getName()
        {
            return NAMES[0];
        }

        public final String[] getNamesRef()
        {
            return NAMES;
        }

        public final String getOid()
        {
            return OID;
        }

        public final boolean isObsolete()
        {
            return false;
        }

        public String getSchema()
        {
            return MetaSchemaConstants.SCHEMA_NAME;
        }

        public void setSchema( String schemaName )
        {
            throw new NotImplementedException();
        }
    }


    public static class NameOrNumericIdSyntax implements LdapSyntax
    {
        private static final long serialVersionUID = 1L;
        private final static String OID = "1.3.6.1.4.1.18060.0.4.0.0.0";
        private final static SyntaxChecker CHECKER = new NumericOidSyntaxChecker();
        private final static String[] NAMES = new String[] { "nameOrOid" };
        
        public final SyntaxChecker getSyntaxChecker() throws NamingException
        {
            return CHECKER;
        }

        public final boolean isHumanReadable()
        {
            return true;
        }

        public final String getDescription()
        {
            return "The syntax for either numeric ids or names.";
        }

        public final String getName()
        {
            return NAMES[0];
        }

        public final String[] getNamesRef()
        {
            return NAMES;
        }

        public final String getOid()
        {
            return OID;
        }

        public final boolean isObsolete()
        {
            return false;
        }

        public String getSchema()
        {
            return MetaSchemaConstants.SCHEMA_NAME;
        }

        public void setSchema( String schemaName )
        {
            throw new NotImplementedException();
        }
    }


    public static class ObjectClassTypeSyntax implements LdapSyntax
    {
        private static final long serialVersionUID = 1L;
        private final static String OID = "1.3.6.1.4.1.18060.0.4.0.0.1";
        private final static SyntaxChecker CHECKER = new ObjectClassTypeSyntaxChecker();
        private final static String[] NAMES = new String[] { "objectClassType" };
        
        public final SyntaxChecker getSyntaxChecker() throws NamingException
        {
            return CHECKER;
        }

        public final boolean isHumanReadable()
        {
            return true;
        }

        public final String getDescription()
        {
            return "The syntax for either numeric ids or names.";
        }

        public final String getName()
        {
            return NAMES[0];
        }

        public final String[] getNamesRef()
        {
            return NAMES;
        }

        public final String getOid()
        {
            return OID;
        }

        public final boolean isObsolete()
        {
            return false;
        }

        public String getSchema()
        {
            return MetaSchemaConstants.SCHEMA_NAME;
        }

        public void setSchema( String schemaName )
        {
            throw new NotImplementedException();
        }
    }


    public static class NumberSyntax implements LdapSyntax
    {
        private static final long serialVersionUID = 1L;
        private final static String OID = "1.3.6.1.4.1.18060.0.4.0.0.4";
        private final static SyntaxChecker CHECKER = new NumericStringSyntaxChecker();
        private final static String[] NAMES = new String[] { "numeric" };
        
        public final SyntaxChecker getSyntaxChecker() throws NamingException
        {
            return CHECKER;
        }

        public final boolean isHumanReadable()
        {
            return true;
        }

        public final String getDescription()
        {
            return "The syntax for numeric strings.";
        }

        public final String getName()
        {
            return NAMES[0];
        }

        public final String[] getNamesRef()
        {
            return NAMES;
        }

        public final String getOid()
        {
            return OID;
        }

        public final boolean isObsolete()
        {
            return false;
        }

        public String getSchema()
        {
            return MetaSchemaConstants.SCHEMA_NAME;
        }

        public void setSchema( String schemaName )
        {
            throw new NotImplementedException();
        }
    }

    public static class ObjectNameSyntax implements LdapSyntax
    {
        private static final long serialVersionUID = 1L;
        private final static String OID = "1.3.6.1.4.1.18060.0.4.0.0.6";
        private final static SyntaxChecker CHECKER = new ObjectNameSyntaxChecker();
        private final static String[] NAMES = new String[] { "objectName" };
        
        public final SyntaxChecker getSyntaxChecker() throws NamingException
        {
            return CHECKER;
        }

        public final boolean isHumanReadable()
        {
            return true;
        }

        public final String getDescription()
        {
            return "The syntax for object names.";
        }

        public final String getName()
        {
            return NAMES[0];
        }

        public final String[] getNamesRef()
        {
            return NAMES;
        }

        public final String getOid()
        {
            return OID;
        }

        public final boolean isObsolete()
        {
            return false;
        }

        public String getSchema()
        {
            return MetaSchemaConstants.SCHEMA_NAME;
        }

        public void setSchema( String schemaName )
        {
            throw new NotImplementedException();
        }
    }
}
