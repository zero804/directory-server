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


import java.util.Comparator;

import javax.naming.NamingException;

import jdbm.helper.StringComparator;

import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.directory.server.constants.MetaSchemaConstants;
import org.apache.directory.server.schema.registries.Registries;
import org.apache.directory.server.schema.registries.SyntaxRegistry;
import org.apache.directory.shared.ldap.NotImplementedException;
import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.schema.MatchingRule;
import org.apache.directory.shared.ldap.schema.Normalizer;
import org.apache.directory.shared.ldap.schema.LdapSyntax;
import org.apache.directory.shared.ldap.schema.comparators.ObjectIdentifierComparator;
import org.apache.directory.shared.ldap.schema.normalizers.DeepTrimToLowerNormalizer;
import org.apache.directory.shared.ldap.schema.normalizers.NoOpNormalizer;
import org.apache.directory.shared.ldap.schema.normalizers.ObjectIdentifierNormalizer;


/**
 * A producer of MatchingRule objects for the apachemeta schema.
 * Modified by hand from generated code
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class ApachemetaMatchingRuleProducer extends AbstractBootstrapProducer
{
    
    
    public ApachemetaMatchingRuleProducer()
    {
        super( ProducerTypeEnum.MATCHING_RULE_PRODUCER );
    }


    // ------------------------------------------------------------------------
    // BootstrapProducer Methods
    // ------------------------------------------------------------------------


    /**
     * @see BootstrapProducer#produce(Registries, ProducerCallback)
     */
    public void produce( Registries registries, ProducerCallback cb )
        throws NamingException
    {
        MatchingRule matchingRule = null;
        
        matchingRule = new NameOrNumericIdMatch( registries.getOidRegistry(), MetaSchemaConstants.SCHEMA_NAME );
        cb.schemaObjectProduced( this, matchingRule.getOid(), matchingRule );
        
        matchingRule = new ObjectClassTypeMatch();
        cb.schemaObjectProduced( this, matchingRule.getOid(), matchingRule );
        
        matchingRule = new NumericOidMatch( registries.getSyntaxRegistry() );
        cb.schemaObjectProduced( this, matchingRule.getOid(), matchingRule );
        
        matchingRule = new SupDITStructureRuleMatch( registries.getSyntaxRegistry() );
        cb.schemaObjectProduced( this, matchingRule.getOid(), matchingRule );
        
        matchingRule = new RuleIdMatch( registries.getSyntaxRegistry() );
        cb.schemaObjectProduced( this, matchingRule.getOid(), matchingRule );
    }
    
    
    public static class RuleIdMatch implements MatchingRule
    {
        private static final long serialVersionUID = 1L;
        private static final String OID = "1.3.6.1.4.1.18060.0.4.0.1.4";
        private final LdapSyntax syntax;
        private final String[] NAMES = new String[] { "ruleIdMatch" };
        
        
        RuleIdMatch( SyntaxRegistry registry ) throws NamingException
        {
            syntax = registry.lookup( SchemaConstants.IA5_STRING_SYNTAX );
        }
        
        public Comparator getComparator() throws NamingException
        {
            return new ComparableComparator();
        }
        
        public Normalizer getNormalizer() throws NamingException
        {
            return new DeepTrimToLowerNormalizer();
        }

        public LdapSyntax getSyntax() throws NamingException
        {
            return syntax;
        }

        public String getDescription()
        {
            return "Rule identifier of this DIT structure rule";
        }

        public String getName()
        {
            return NAMES[0];
        }

        public String[] getNamesRef()
        {
            return NAMES;
        }

        public String getOid()
        {
            return OID;
        }

        public boolean isObsolete()
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
    
    
    public static class SupDITStructureRuleMatch implements MatchingRule
    {
        private static final String OID = "1.3.6.1.4.1.18060.0.4.0.1.3";
        private static final long serialVersionUID = 1L;
        String[] NAMES = new String[] { "supDITStructureRuleMatch" };
        LdapSyntax syntax;
        
        
        public SupDITStructureRuleMatch( SyntaxRegistry registry ) throws NamingException
        {
            this.syntax = registry.lookup( SchemaConstants.DIT_STRUCTURE_RULE_SYNTAX );
        }
        
        
        public Comparator getComparator() throws NamingException
        {
            return new StringComparator();
        }

        public Normalizer getNormalizer() throws NamingException
        {
            return new DeepTrimToLowerNormalizer();
        }

        public LdapSyntax getSyntax() throws NamingException
        {
            return syntax;
        }

        public String getDescription()
        {
            return "A matching rule matching dit structure rule attributes";
        }

        public String getName()
        {
            return NAMES[0];
        }

        public String[] getNamesRef()
        {
            return NAMES;
        }

        public String getOid()
        {
            return OID;
        }

        public boolean isObsolete()
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
    
    
    public static class NumericOidMatch implements MatchingRule
    {
        private static final String OID = "1.3.6.1.4.1.18060.0.4.0.1.2";

        private static final long serialVersionUID = 1L;

        final String[] NAMES = new String[] { "numericOidMatch" };
        LdapSyntax syntax;
        
        public NumericOidMatch( SyntaxRegistry registry ) throws NamingException
        {
            this.syntax = registry.lookup( SchemaConstants.OID_SYNTAX );
        }
        
        public Comparator getComparator() throws NamingException
        {
            return new ObjectIdentifierComparator();
        }

        public Normalizer getNormalizer() throws NamingException
        {
            return new ObjectIdentifierNormalizer();
        }

        public LdapSyntax getSyntax() throws NamingException
        {
            return syntax;
        }

        public String getDescription()
        {
            return "a matching rule for numeric oids";
        }

        public String getName()
        {
            return NAMES[0];
        }

        public String[] getNamesRef()
        {
            return NAMES;
        }

        public String getOid()
        {
            return OID;
        }

        public boolean isObsolete()
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

    
    public static class ObjectClassTypeMatch implements MatchingRule
    {
        private static final long serialVersionUID = 1L;
        public static final Comparator COMPARATOR = new ApachemetaComparatorProducer.ObjectClassTypeComparator(); 
        public static final Normalizer NORMALIZER = new NoOpNormalizer();
        public static final LdapSyntax SYNTAX = new ApachemetaSyntaxProducer.ObjectClassTypeSyntax();
        public static final String OID = "1.3.6.1.4.1.18060.0.4.0.1.1";
        
        private static final String[] NAMES = new String[] { "objectClassTypeMatch" };
        
        public Comparator getComparator() throws NamingException
        {
            return COMPARATOR;
        }

        
        public Normalizer getNormalizer() throws NamingException
        {
            return NORMALIZER;
        }

        public LdapSyntax getSyntax() throws NamingException
        {
            return SYNTAX;
        }

        public String getDescription()
        {
            return "objectClassTypeMatch: for mathing AUXILIARY, STRUCTURAL, ABSTRACT";
        }

        public String getName()
        {
            return NAMES[0];
        }

        public String[] getNamesRef()
        {
            return NAMES;
        }

        public String getOid()
        {
            return OID;
        }

        public boolean isObsolete()
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
