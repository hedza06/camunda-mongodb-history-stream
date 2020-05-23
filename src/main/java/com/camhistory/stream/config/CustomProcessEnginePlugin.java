package com.camhistory.stream.config;

import com.camhistory.stream.handlers.CustomHistoryEventHandler;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.impl.history.handler.CompositeDbHistoryEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CustomProcessEnginePlugin implements ProcessEnginePlugin {

    @Autowired
    private CustomHistoryEventHandler customHistoryEventHandler;


    @Override
    public void preInit(ProcessEngineConfigurationImpl processEngineConfiguration)
    {
        List<HistoryLevel> customHistoryLevels = processEngineConfiguration.getCustomHistoryLevels();
        if (customHistoryLevels == null)
        {
            customHistoryLevels = new ArrayList<>();
            processEngineConfiguration.setCustomHistoryLevels(customHistoryLevels);
        }
        customHistoryLevels.add(CustomHistoryLevel.getInstance());
    }

    @Override
    public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration)
    {
        processEngineConfiguration.setHistoryEventHandler(
            new CompositeDbHistoryEventHandler(customHistoryEventHandler)
        );
    }

    @Override
    public void postProcessEngineBuild(ProcessEngine processEngine) {
        // no implementation...
    }
}
