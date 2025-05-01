package ro.mpp2024.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;


public abstract class AbsConcurrentServer extends AbstractServer {
    private static final Logger logger = LogManager.getLogger(AbsConcurrentServer.class);

    public AbsConcurrentServer(int port) {
        super(port);
        logger.debug("Concurrent AbstractServer");
    }

    @Override
    protected void processRequest(Socket client) {
        Thread tw = createWorker(client);
        tw.start();
    }

    protected abstract Thread createWorker(Socket client) ;
}
