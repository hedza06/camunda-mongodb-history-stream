package com.camhistory.stream.config;

import com.camhistory.stream.handlers.CustomHistoryEventHandler;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.ProcessEnginePlugin;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.impl.history.handler.CompositeDbHistoryEventHandler;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CustomProcessEnginePlugin implements ProcessEnginePlugin {

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
        processEngineConfiguration.setHistoryEventHandler(
            new CompositeDbHistoryEventHandler(CustomHistoryEventHandler.getInstance())
        );
        // processEngineConfiguration.setHistoryEventHandler(CustomHistoryEventHandler.getInstance());
    }

    @Override
    public void postInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
        // no implementation...
    }

    @Override
    public void postProcessEngineBuild(ProcessEngine processEngine) {
        // no implementation...
    }
}
