package org.erachain.service;

import org.apache.flink.api.java.tuple.Tuple2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BeanLoader implements BeanDefinitionRegistryPostProcessor {

    @Autowired
    private DataBeanLoader dataBeanLoader;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        dataBeanLoader.getAccountList().
                forEach((account) -> {
                    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(account.getClassName()).setLazyInit(true);
                    String service = account.getClassName().concat("Service");
                    registry.registerBeanDefinition(service, builder.getBeanDefinition());
                    dataBeanLoader.getNamesObjectsServices().add(Tuple2.of(account.getAccountUrl(), service));
                });
//        String[] beanDefinitionNames = registry.getBeanDefinitionNames();

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    public List<Tuple2<String, String>> getNamesObjectsServices() {
        return dataBeanLoader.getNamesObjectsServices();
    }
}
