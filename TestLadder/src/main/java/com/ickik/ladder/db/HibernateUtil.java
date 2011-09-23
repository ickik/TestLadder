package com.ickik.ladder.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.ickik.ladder.utils.LadderProperties;
import com.ickik.ladder.utils.TestLadderEncoding;
import com.ickik.ladder.utils.TestLadderProperties;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration c = new Configuration();
        	//c.setProperties(TestLadderProperties.getInstance().getProperties());
        	String server = TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_NAME.toString());
        	int portNumber = Integer.parseInt(TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_PORT.toString()));
        	String dataBase = TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.DATABASE.toString());
        	String login = TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_LOGIN.toString());
        	String password = TestLadderEncoding.decryptionPassword(TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_LOGIN.toString()), TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.SERVER_PASSWORD.toString()));
        	Database db  = Database.valueOf(TestLadderProperties.getInstance().getProperties().getProperty(LadderProperties.DATABASE_TYPE.toString()));
        	c.setProperty("hibernate.connection.driver_class", db.getDriverName());
        	c.setProperty("hibernate.connection.url", "jdbc:" + db.toString().toLowerCase() + "://" + server + ":" + Integer.toString(portNumber) + "/" + dataBase);
        	c.setProperty("hibernate.connection.username", login);
        	c.setProperty("hibernate.connection.password", password);
        	c.setProperty("hibernate.dialect", db.getDBDialect());
        	c.setProperty("hibernate.connection.pool_size", Integer.toString(1));
        	//c.setProperty("hibernate.connection.datasource", "java:/src/main/resources");
        	return c.configure().buildSessionFactory();
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
