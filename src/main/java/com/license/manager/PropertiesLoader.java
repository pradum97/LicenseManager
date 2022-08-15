package com.license.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesLoader {

    public Properties getInsertProp() {
        String path = "util/url.properties";
        InputStream is = getClass().getResourceAsStream(path);
        Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }
}
