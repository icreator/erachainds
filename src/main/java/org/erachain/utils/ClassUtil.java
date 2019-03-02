package org.erachain.utils;

import org.erachain.entities.BaseEntity;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

//@Service
@PropertySource("classpath:custom.properties")
public class ClassUtil {

    @Value( "${CLASSES_URL}" )
	private String CustomClassUrl;

    @Autowired
    private Logger logger;

	public  BaseEntity getInstFromClass(Class loadedClass) {

        Object inst = null;

        try {
            inst = Arrays.stream(loadedClass.getConstructors()).filter(constr -> constr.getParameterCount() == 0)
                  .findFirst().orElseThrow(IllegalArgumentException::new).newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }

        return (BaseEntity) inst;
	}

	public  Class load(String name)  {

        URL url = null;
        try {
            url = new URL(CustomClassUrl);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        }
        URL[] urls = new URL[]{url};
        URLClassLoader cl = new URLClassLoader(urls, ClassUtil.class.getClassLoader());
        Class loadedClass = null;
        try {
            loadedClass = cl.loadClass("org.erachain.entities." + name);
        }
        catch (ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        return loadedClass;

	}

}
