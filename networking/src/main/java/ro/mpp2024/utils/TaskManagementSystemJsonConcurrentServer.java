package ro.mpp2024.utils;

import ro.mpp2024.IServices;
import ro.mpp2024.jsonProtocol.TaskManagementSystemJsonWorker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import ro.mpp2024.services.TaskManagementSystemServicesImpl;

import java.net.Socket;

public class TaskManagementSystemJsonConcurrentServer extends AbsConcurrentServer {
    private final IServices systemServer;
    private static final Logger logger = LogManager.getLogger(TaskManagementSystemJsonConcurrentServer.class);

    public TaskManagementSystemJsonConcurrentServer(int port, IServices systemServer) {
        super(port);
        this.systemServer= systemServer;
        logger.info("TaskManagementSystemJsonConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        TaskManagementSystemJsonWorker worker = new TaskManagementSystemJsonWorker(systemServer, client);
        return new Thread(worker);
    }
}

