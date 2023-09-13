package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyLoader{
    public static Properties loadProperties() throws IOException {
        Properties configuration = new Properties();
        InputStream inputStream = PropertyLoader.class.getClassLoader().getResourceAsStream("application.properties");
        configuration.load(inputStream);
        inputStream.close();
        return configuration;
    }
}
