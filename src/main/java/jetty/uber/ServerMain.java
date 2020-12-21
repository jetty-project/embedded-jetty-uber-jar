//
//  ========================================================================
//  Copyright (c) Mort Bay Consulting Pty Ltd and others.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package jetty.uber;

import java.net.URI;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.websocket.javax.server.config.JavaxWebSocketServletContainerInitializer;

public class ServerMain
{
    public static void main(String[] args) throws Throwable
    {

        try
        {
            new ServerMain().run();
        }
        catch (Throwable t)
        {
            t.printStackTrace();
        }
    }

    public void run() throws Exception
    {
        Server server = new Server(8080);

        URL webRootLocation = this.getClass().getResource("/webroot/index.html");
        if (webRootLocation == null)
        {
            throw new IllegalStateException("Unable to determine webroot URL location");
        }

        URI webRootUri = URI.create(webRootLocation.toURI().toASCIIString().replaceFirst("/index.html$", "/"));
        System.err.printf("Web Root URI: %s%n", webRootUri);

        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setContextPath("/");
        contextHandler.setBaseResource(Resource.newResource(webRootUri));
        contextHandler.setWelcomeFiles(new String[]{"index.html"});

        contextHandler.getMimeTypes().addMimeMapping("txt", "text/plain;charset=utf-8");

        server.setHandler(contextHandler);

        // Add WebSocket endpoints
        JavaxWebSocketServletContainerInitializer.configure(contextHandler, (context, wsContainer) ->
            wsContainer.addEndpoint(TimeSocket.class));

        // Add Servlet endpoints
        contextHandler.addServlet(TimeServlet.class, "/time/");
        contextHandler.addServlet(DefaultServlet.class, "/");

        // Start Server
        server.start();
        server.join();
    }
}
