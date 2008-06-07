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
package org.apache.directory.server.ldap;


import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.spi.InitialContextFactory;

import org.apache.directory.server.core.jndi.ServerLdapContext;
import org.apache.directory.shared.ldap.constants.AuthenticationLevel;
import org.apache.directory.shared.ldap.exception.LdapNoPermissionException;
import org.apache.directory.shared.ldap.message.AbandonableRequest;
import org.apache.directory.shared.ldap.message.Request;
import org.apache.mina.common.IoSession;


/**
 * A client session state based on JNDI contexts.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class SessionRegistry
{
    /** the set of client contexts */
    private final Map<IoSession, LdapContext> contexts = new HashMap<IoSession, LdapContext>();

    /** outstanding requests for a session */
    private final Map<IoSession, Map<Integer, Request>> requests = new HashMap<IoSession, Map<Integer, Request>>();

    /** the properties associated with this SessionRegistry */
    private Hashtable<String, Object> env;

    /** the configuration associated with this SessionRegistry */
    private LdapServer ldapServer;


    /**
     * Creates a singleton session state object for the system.
     *
     * @param ldapServer the ldap server instance
     * @param env the properties associated with this SessionRegistry
     */
    public SessionRegistry( LdapServer ldapServer, Hashtable<String, Object> env )
    {
        this.ldapServer = ldapServer;

        if ( env == null )
        {
            this.env = new Hashtable<String, Object>();
            this.env.put( Context.PROVIDER_URL, "" );
            this.env.put( Context.INITIAL_CONTEXT_FACTORY, "org.apache.directory.server.jndi.ServerContextFactory" );
        }
        else
        {
            this.env = env;
            this.env.put( Context.PROVIDER_URL, "" );
        }
    }


    /**
     * Gets a cloned copy of the environment associated with this registry.
     *
     * @return the registry environment
     */
    @SuppressWarnings( "unchecked" )
    public Hashtable<String, Object> getEnvironmentByCopy()
    {
        return ( Hashtable<String, Object> ) env.clone();
    }


    /**
     * Adds a request to the map of outstanding requests for a session.
     * 
     * @param session the session the request was issued on
     * @param req the request to add
     */
    public void addOutstandingRequest( IoSession session, Request req )
    {
        // pull out the map of requests by id
        synchronized ( requests )
        {
            Map<Integer, Request> reqmap = requests.get( session );
            
            if ( reqmap == null )
            {
                reqmap = new HashMap<Integer, Request>();
                requests.put( session, reqmap );
            }
            
            reqmap.put( req.getMessageId(), req );
        }
    }


    /**
     * Overload that does not require boxing of primitive messageId.
     * 
     * @param session the session associated with the request
     * @param messageId the id of the request
     * @return the Request if it is removed or null if no such request was mapped as outstanding
     */
    public Request removeOutstandingRequest( IoSession session, int messageId )
    {
        return removeOutstandingRequest( session, new Integer( messageId ) );
    }


    /**
     * Removes an outstanding request from the session's outstanding request map.
     * 
     * @param session the session the request is removed from
     * @param id the messageId of the request to remove
     * @return the Request if it is removed or null if no such request was mapped as outstanding
     */
    public Request removeOutstandingRequest( IoSession session, Integer id )
    {
        // pull out the map of requests by id
        synchronized ( requests )
        {
            Map<Integer, Request> reqmap = requests.get( session );
            
            if ( reqmap == null )
            {
                return null;
            }
            
            return reqmap.remove( id );
        }
    }


    /**
     * Returns a shallow copied map of all outstanding requests for an IoSession.
     * 
     * @param session the session to get outstanding requests for
     * @return a map by message id as an Integer to Request objects
     */
    public Map<Integer, Request> getOutstandingRequests( IoSession session )
    {
        Map<Integer, Request> reqmap = requests.get( session );
        
        if ( reqmap == null )
        {
            //noinspection unchecked
            return Collections.EMPTY_MAP;
        }
        
        // Copy the maps
        return new HashMap<Integer, Request>( reqmap );
    }


    /**
     * Overload that does not require boxing of primitive messageId.
     * 
     * @param session the session associated with the request
     * @param abandonedId the id of the request
     * @return the request in session for id or null if request has completed
     */
    public Request getOutstandingRequest( IoSession session, int abandonedId )
    {
        return getOutstandingRequest( session, new Integer( abandonedId ) );
    }


    /**
     * Gets an outstanding request by messageId for a session.
     * 
     * @param session the LDAP session 
     * @param id the message id of the request
     * @return the request in session for id or null if request has completed
     */
    public Request getOutstandingRequest( IoSession session, Integer id )
    {
        Map<Integer, Request> reqmap = requests.get( session );
        
        if ( reqmap == null )
        {
            return null;
        }
        
        return reqmap.get( id );
    }


    public IoSession[] getSessions()
    {
        IoSession[] sessions;
        
        synchronized ( contexts )
        {
            sessions = new IoSession[contexts.size()];
            sessions = contexts.keySet().toArray( sessions );
        }
        
        return sessions;
    }


    /**
     * Gets the InitialContext to the root of the system that was gotten for
     * client.  If the context is not present then there was no bind operation
     * that set it.  Hence this operation requesting the context is anonymous.
     *
     * @todo this allowAnonymous parameter is a bit confusing - figure out
     * something better to call it.  I think only bind requests a context
     * that is not anonymous.  Have to refactor the heck out of this lousy code.
     * 
     * @param session the client's key
     * @param connCtls connection controls if any to use if creating anon context
     * @param allowAnonymous true if anonymous requests will create anonymous
     * InitialContext if one is not present for the operation
     * @return the InitialContext or null
     * @throws NamingException if something goes wrong
     */
    public LdapContext getLdapContext( IoSession session, Control[] connCtls, boolean allowAnonymous )
        throws NamingException
    {
        LdapContext ctx;

        synchronized ( contexts )
        {
            ctx = contexts.get( session );
        }

        // there is no context so its an implicit bind, no bind operation is being performed
        if ( ctx == null && allowAnonymous )
        {
            // if configuration says disable anonymous binds we throw exception
            if ( ! ldapServer.isAllowAnonymousAccess() )
            {
                throw new LdapNoPermissionException( "Anonymous binds have been disabled!" );
            }

            if ( env.containsKey( "server.use.factory.instance" ) )
            {
                InitialContextFactory factory = ( InitialContextFactory ) env.get( "server.use.factory.instance" );

                if ( factory == null )
                {
                    throw new NullPointerException( "server.use.factory.instance was set in env but was null" );
                }

                ctx = ( LdapContext ) factory.getInitialContext( env );
            }
            else
            {
                //noinspection unchecked
                Hashtable<String, Object> cloned = ( Hashtable<String, Object> ) env.clone();
                cloned.put( Context.SECURITY_AUTHENTICATION, AuthenticationLevel.NONE.toString() );
                cloned.remove( Context.SECURITY_PRINCIPAL );
                cloned.remove( Context.SECURITY_CREDENTIALS );
                ctx = new InitialLdapContext( cloned, connCtls );
            }
        }
        // the context came up non null so we binded explicitly and op now is not bind
        else if ( ctx != null && allowAnonymous )
        {
            ServerLdapContext slc;
            
            if ( !( ctx instanceof ServerLdapContext ) )
            {
                slc = ( ServerLdapContext ) ctx.lookup( "" );
            }
            else
            {
                slc = ( ServerLdapContext ) ctx;
            }
            
            boolean isAnonymousUser = slc.getSession().getEffectivePrincipal().getName().trim().equals( "" );

            // if the user principal is anonymous and the configuration does not allow anonymous binds we
            // prevent the operation by blowing a NoPermissionsException
            if ( isAnonymousUser && ! ldapServer.isAllowAnonymousAccess() )
            {
                throw new LdapNoPermissionException( "Anonymous binds have been disabled!" );
            }
        }

        return ctx;
    }


    /**
     * Gets the InitialContext to the root of the system that was gotten for
     * client ONLY to be used for RootDSE Search operations.  This bypasses
     * checks to only allow anonymous binds for this special case.
     *
     * @param session the client's key
     * @param connCtls connection controls if any to use if creating anon context
     * @return the InitialContext or null
     * @throws NamingException if something goes wrong
     */
    public LdapContext getLdapContextOnRootDSEAccess( IoSession session, Control[] connCtls ) throws NamingException
    {
        LdapContext ctx;

        synchronized ( contexts )
        {
            ctx = contexts.get( session );
        }

        if ( ctx == null )
        {
            if ( env.containsKey( "server.use.factory.instance" ) )
            {
                InitialContextFactory factory = ( InitialContextFactory ) env.get( "server.use.factory.instance" );

                if ( factory == null )
                {
                    throw new NullPointerException( "server.use.factory.instance was set in env but was null" );
                }

                ctx = ( LdapContext ) factory.getInitialContext( env );
            }
            else
            {
                //noinspection unchecked
                Hashtable<String, Object> cloned = ( Hashtable<String, Object> ) env.clone();
                cloned.put( Context.SECURITY_AUTHENTICATION, AuthenticationLevel.NONE.toString() );
                cloned.remove( Context.SECURITY_PRINCIPAL );
                cloned.remove( Context.SECURITY_CREDENTIALS );
                ctx = new InitialLdapContext( cloned, connCtls );
            }
        }

        return ctx;
    }


    /**
     * Sets the initial context associated with a newly authenticated client.
     *
     * @param session the client session
     * @param ictx the initial context gotten
     */
    public void setLdapContext( IoSession session, LdapContext ictx )
    {
        synchronized ( contexts )
        {
            contexts.put( session, ictx );
        }
    }


    /**
     * Removes the state mapping a JNDI initial context for the client's key.
     *
     * @param session the client's key
     */
    public void remove( IoSession session )
    {
        synchronized ( contexts )
        {
            contexts.remove( session );
        }

        Map<Integer, Request> reqmap;
        
        synchronized ( requests )
        {
            reqmap = requests.remove( session );
        }

        if ( reqmap == null || reqmap.isEmpty() )
        {
            return;
        }

        for ( Request request : reqmap.values() )
        {
            if ( request instanceof AbandonableRequest )
            {
                ( ( AbandonableRequest ) request ).abandon();
            }
        }
    }


    /**
     * Terminates the session by publishing a disconnect event.
     *
     * @param session the client key of the client to disconnect
     */
    public void terminateSession( IoSession session )
    {
        session.close();
    }
}
