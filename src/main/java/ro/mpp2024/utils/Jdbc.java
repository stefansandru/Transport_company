package ro.mpp2024.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Jdbc {
    private final String url;
    private final String user;
    private final String password;
    private static final Logger logger = LogManager.getLogger(Jdbc.class);

    public Jdbc(Properties props) {
        if (props != null) {
            this.url = props.getProperty("jdbc.url");
            this.user = props.getProperty("jdbc.username");
            this.password = props.getProperty("jdbc.password");

            try {
                // Load the JDBC driver if specified
                String driver = props.getProperty("jdbc.driver");
                if (driver != null) {
                    Class.forName(driver);
                    logger.info("Loaded database driver: {}", driver);
                }
            } catch (ClassNotFoundException e) {
                logger.error("Error loading database driver", e);
            }
        } else {
            this.url = null;
            this.user = null;
            this.password = null;
            logger.warn("Initializing Jdbc with null properties");
        }

        logger.info("Jdbc initialized with URL: {}", url);
    }

    public Connection getConnection() throws SQLException {
        logger.debug("Getting database connection");
        return DriverManager.getConnection(url, user, password);
    }
}