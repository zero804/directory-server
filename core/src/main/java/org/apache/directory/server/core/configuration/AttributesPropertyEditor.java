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
package org.apache.directory.server.core.configuration;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.shared.ldap.ldif.LdifComposer;
import org.apache.directory.shared.ldap.ldif.LdifComposerImpl;
import org.apache.directory.shared.ldap.ldif.LdifReader;
import org.apache.directory.shared.ldap.message.AttributesImpl;
import org.apache.directory.shared.ldap.util.StringTools;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;


/**
 * A JavaBeans {@link PropertyEditor} that can convert {@link Attributes} to
 * LDIF string and vice versa. This class is useful when you're going to
 * configure a {@link DirectoryService} with 3rd party containers such as <a
 * href="http://www.springframework.org/">Spring Framework</a>.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class AttributesPropertyEditor extends PropertyEditorSupport
{

    /**
     * Creates a new instance.
     */
    public AttributesPropertyEditor()
    {
        super();
    }

    /**
     * Creates a new instance with source object.
     */
    public AttributesPropertyEditor( Object source )
    {
        super( source );
    }

    /**
     * Returns LDIF string of {@link Attributes} object.
     */
    @SuppressWarnings("deprecation")
    public String getAsText()
    {
        LdifComposer composer = new LdifComposerImpl();
        Map<String, Object> map = new MultiValueMap();

        Attributes attrs = (Attributes) getValue();
        try
        {
            NamingEnumeration<? extends Attribute> e = attrs.getAll();
            while ( e.hasMore() )
            {
                Attribute attr = e.next();
                NamingEnumeration<? extends Object> e2 = attr.getAll();
                while ( e2.hasMoreElements() )
                {
                    Object value = e2.next();
                    map.put( attr.getID(), value );
                }
            }

            return composer.compose( map );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Read an entry (without DN)
     * 
     * @param text
     *            The ldif format file
     * @return An Attributes.
     */
    private Attributes readEntry( String text )
    {
        StringReader strIn = new StringReader( text );
        BufferedReader in = new BufferedReader( strIn );

        String line = null;
        Attributes attributes = new AttributesImpl( true );

        try
        {
            while ( ( line = in.readLine() ) != null )
            {
                if ( line.length() == 0 )
                {
                    continue;
                }

                String addedLine = line.trim();

                if ( StringTools.isEmpty( addedLine ) )
                {
                    continue;
                }

                Attribute attribute = LdifReader.parseAttributeValue( addedLine );
                Attribute oldAttribute = attributes.get( attribute.getID() );

                if ( oldAttribute != null )
                {
                    try
                    {
                        oldAttribute.add( attribute.get() );
                        attributes.put( oldAttribute );
                    }
                    catch (NamingException ne)
                    {
                        // Do nothing
                    }
                }
                else
                {
                    attributes.put( attribute );
                }
            }
        }
        catch (IOException ioe)
        {
            // Do nothing : we can't reach this point !
        }

        return attributes;
    }

    /**
     * Converts the specified LDIF string into {@link Attributes}.
     */
    public void setAsText( String text ) throws IllegalArgumentException
    {
        if ( text == null )
        {
            text = "";
        }

        setValue( readEntry( text ) );
    }
}
