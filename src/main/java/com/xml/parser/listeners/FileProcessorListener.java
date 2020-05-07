package com.xml.parser.listeners;


import com.xml.parser.model.Config;
import org.springframework.batch.core.ItemProcessListener;

public class FileProcessorListener implements ItemProcessListener<Config, Config> {


    @Override
    public void beforeProcess(Config config) {

    }

    @Override
    public void afterProcess(Config config, Config config2) {

    }

    @Override
    public void onProcessError(Config config, Exception e) {
        System.out.println(config);
        System.out.println(e);
    }
}
