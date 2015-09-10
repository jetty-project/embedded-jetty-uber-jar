package jetty.uber;

import java.net.URI;
import java.net.URL;

import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

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

        URI webRootUri = URI.create(webRootLocation.toURI().toASCIIString().replaceFirst("/index.html$","/"));
        System.err.printf("Web Root URI: %s%n",webRootUri);

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.setBaseResource(Resource.newResource(webRootUri));
        context.setWelcomeFiles(new String[] { "index.html" });

        context.getMimeTypes().addMimeMapping("txt","text/plain;charset=utf-8");

        server.setHandler(context);

        // Add WebSocket endpoints
        ServerContainer wsContainer = WebSocketServerContainerInitializer.configureContext(context);
        wsContainer.addEndpoint(TimeSocket.class);

        // Add Servlet endpoints
        context.addServlet(TimeServlet.class,"/time/");

        context.addServlet(DefaultServlet.class,"/");

        // Start Server
        server.start();
        server.join();
    }
}
