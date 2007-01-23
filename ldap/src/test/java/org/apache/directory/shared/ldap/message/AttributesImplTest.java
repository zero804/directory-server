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
package org.apache.directory.shared.ldap.message;


import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

import junit.framework.TestCase;

import org.apache.directory.shared.ldap.message.AttributeImpl;
import org.apache.directory.shared.ldap.message.AttributesImpl;
import org.apache.directory.shared.ldap.util.ArrayUtils;

/**
 * Test cases for the methods of the LockableAttributeImpl class.
 * 
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory Project</a>
 *         $Rev$
 */
public class AttributesImplTest extends TestCase
{
    /**
     * Creates and populates a LockableAttributeImpl with a specific id.
     * 
     * @param id
     *            the id for the attribute
     * @return the LockableAttributeImpl assembled for testing
     */
    private AttributeImpl getAttribute( String id )
    {
        AttributeImpl attr = new AttributeImpl( id );
        attr.add( "value0" );
        attr.add( "value1" );
        attr.add( "value2" );
        return attr;
    }


    /**
     * Creates and populates a LockableAttributes object
     * 
     * @return
     */
    private AttributesImpl getAttributes()
    {
        AttributesImpl attrs = new AttributesImpl();
        attrs.put( getAttribute( "attr0" ) );
        attrs.put( getAttribute( "attr1" ) );
        attrs.put( getAttribute( "attr2" ) );
        return attrs;
    }


    /**
     * Tests that toString works properly.
     */
    public void testToString()
    {
        AttributesImpl attrs = getAttributes();
        attrs.put( "binaryNullAttr", null );
        attrs.put( "binaryEmptyAttr", ArrayUtils.EMPTY_BYTE_ARRAY );
        attrs.put( "binaryFullAttr", new byte[]
            { 0x44, 0x23 } );
        String str = attrs.toString();
        assertTrue( str.indexOf( "binaryNullAttr" ) != -1 );
        assertTrue( str.indexOf( "binaryEmptyAttr" ) != -1 );
        assertTrue( str.indexOf( "binaryFullAttr" ) != -1 );
    }


    /**
     * Tests equality for the same object reference.
     */
    public void testEqualsSameObj()
    {
        AttributesImpl attrs = getAttributes();
        assertTrue( "same object should return true", attrs.equals( attrs ) );
    }


    /**
     * Tests two exact replicas for equality.
     */
    public void testEqualsExactCopy()
    {
        AttributesImpl attrs0 = getAttributes();
        AttributesImpl attrs1 = getAttributes();
        assertTrue( "exact copies should be equal", attrs0.equals( attrs1 ) );
        assertTrue( "exact copies should be equal", attrs1.equals( attrs0 ) );
    }


    /**
     * Tests two exact copies with multiple copies of the same attribute added
     * for equality.
     */
    public void testEqualsExactCopyWithReplicas()
    {
        AttributesImpl attrs0 = getAttributes();
        AttributesImpl attrs1 = getAttributes();
        attrs1.put( getAttribute( "attr0" ) );
        attrs1.put( getAttribute( "attr0" ) );
        assertTrue( "exact copies with repeated adds should be equal", attrs0.equals( attrs1 ) );
        assertTrue( "exact copies with repeated adds should be equal", attrs1.equals( attrs0 ) );
    }


    /**
     * Test inequality with different attributes.
     */
    public void testNotEqualDiffAttr()
    {
        AttributesImpl attrs0 = getAttributes();
        AttributesImpl attrs1 = getAttributes();
        attrs1.put( getAttribute( "blah" ) );
        assertFalse( "different attributes should not be equal", attrs0.equals( attrs1 ) );
        assertFalse( "different attributes should not be equal", attrs1.equals( attrs0 ) );
    }


    /**
     * Tests equality of same attributes with different implementations.
     */
    public void testEqualsDiffImpl()
    {
        AttributesImpl attrs0 = getAttributes();
        Attributes attrs1 = new AttributesImpl( true );
        attrs1.put( getAttribute( "attr0" ) );
        attrs1.put( getAttribute( "attr1" ) );
        attrs1.put( getAttribute( "attr2" ) );

        assertTrue( "different implementations of the same " + "attributes should be equal", attrs0.equals( attrs1 ) );
        assertTrue( "different implementations of the same " + "attributes should be equal", attrs1.equals( attrs0 ) );
    }


    public void testCompareToBasicAttributes()
    {
        AttributesImpl attrs0 = new AttributesImpl();
        attrs0.put( "attr0", "value0" );
        attrs0.put( "attr1", "value1" );

        Attribute attr0 = new AttributeImpl( "attr2" );
        attr0.add( "value2" );
        attr0.add( "value3" );
        attrs0.put( attr0 );

        Attributes attrs1 = new AttributesImpl( true );
        attrs1.put( "attr0", "value0" );
        attrs1.put( "attr1", "value1" );

        Attribute attr1 = new AttributeImpl( "attr2" );
        attr1.add( "value2" );
        attr1.add( "value3" );
        attrs1.put( attr1 );

        assertTrue( "both implementations should produce the same outcome", attrs0.equals( attrs1 ) );
    }
    
    public void testCloneAttributes() throws NamingException
    {
        AttributesImpl attrs = new AttributesImpl();
        attrs.put( "attr0", "value0" );
        attrs.put( "attr1", "value1" );
        
        Attribute attr = new AttributeImpl( "attr2" );
        attr.add( "value2" );
        attr.add( "value3" );
        attrs.put( attr );
        
        AttributesImpl clone = (AttributesImpl)attrs.clone();
        
        // Both attributes should be equals
        assertEquals( attrs, clone );
        assertEquals( 3, clone.size() );
        
        Attribute attr0 = clone.get( "attr0" );
        assertEquals( 1, attr0.size() );
        assertEquals( "value0", attr0.get() );
        
        Attribute attr1 = clone.get( "attr1" );
        assertEquals( 1, attr1.size() );
        assertEquals( "value1", attr1.get() );
        
        Attribute attr2 = clone.get( "attr2" );
        assertEquals( 2, attr2.size() );
        assertTrue( attr2.contains( "value2" ) );
        assertTrue( attr2.contains( "value3" ) );
        
        // Remove an element
        attrs.remove( "attr1" );
        assertNotSame( attrs, clone );
        assertEquals( 2, attrs.size() );
        assertEquals( 3, clone.size() );
    }

    public void testCopyBasicAttributes() throws NamingException
    {
        BasicAttributes attrs = new BasicAttributes();
        attrs.put( "attr0", "value0" );
        attrs.put( "attr1", "value1" );

        Attribute attr = new AttributeImpl( "attr2" );
        attr.add( "value2" );
        attr.add( "value3" );
        attrs.put( attr );
        
        AttributesImpl copy = new AttributesImpl( attrs );
        
        // Both attributes should be internally equals
        assertNotSame( copy, attrs );
        assertEquals( 3, copy.size() );
        
        Attribute attr0 = copy.get( "attr0" );
        assertEquals( 1, attr0.size() );
        assertEquals( "value0", attr0.get() );
        
        Attribute attr1 = copy.get( "attr1" );
        assertEquals( 1, attr1.size() );
        assertEquals( "value1", attr1.get() );
        
        Attribute attr2 = copy.get( "attr2" );
        assertEquals( 2, attr2.size() );
        assertTrue( attr2.contains( "value2" ) );
        assertTrue( attr2.contains( "value3" ) );
        
        // Remove an element
        attrs.remove( "attr1" );
        assertNotSame( copy, attrs );
        assertEquals( 2, attrs.size() );
        assertEquals( 3, copy.size() );
    }
}
