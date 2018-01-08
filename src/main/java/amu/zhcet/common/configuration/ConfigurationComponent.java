package amu.zhcet.common.configuration;

import amu.zhcet.ApplicationProperties;
import amu.zhcet.data.config.Configuration;
import amu.zhcet.data.config.ConfigurationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ConfigurationComponent {

    @Autowired
    public ConfigurationComponent(ConfigurationRepository configurationRepository, ApplicationProperties applicationProperties) {
        log.info("Checking default configuration of application");
        Optional<Configuration> configurationOptional = configurationRepository.findById(0);

        configurationOptional.ifPresent(configuration -> log.info("Configuration already present : " + configuration));
        configurationOptional.orElseGet(() -> {
            log.warn("Default configuration absent... Building new config");
            Configuration defaultConfiguration = new Configuration();
            defaultConfiguration.setId(0L);
            defaultConfiguration.setUrl(applicationProperties.getUrl());

            configurationRepository.save(defaultConfiguration);
            log.warn("Saved default configuration : " + defaultConfiguration);

            return defaultConfiguration;
        });
    }

}