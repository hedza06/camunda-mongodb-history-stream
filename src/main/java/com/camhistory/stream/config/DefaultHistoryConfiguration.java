package com.camhistory.stream.config;

import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.spring.boot.starter.configuration.impl.AbstractCamundaConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultHistoryConfiguration extends AbstractCamundaConfiguration implements CamundaHistoryConfiguration {

    @Autowired
    private CustomHistoryFilterHandler historyEventHandler;

    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration)
    {
        String historyLevel = camundaBpmProperties.getHistoryLevel();
        if (historyLevel != null) {
            processEngineConfiguration.setHistory(historyLevel);
        }
        if (historyEventHandler != null) {
            processEngineConfiguration.setHistoryEventHandler(historyEventHandler);
        }
    }
}
