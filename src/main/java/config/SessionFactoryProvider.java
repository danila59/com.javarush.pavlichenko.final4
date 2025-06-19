package config;

import org.hibernate.SessionFactory;

public interface SessionFactoryProvider {

    SessionFactory getSessionFactory();

}
