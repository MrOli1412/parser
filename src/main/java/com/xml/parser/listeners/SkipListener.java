package com.xml.parser.listeners;

import com.xml.parser.model.Config;

public class SkipListener implements org.springframework.batch.core.SkipListener<Config, Config> {
    @Override
    public void onSkipInRead(Throwable t) {

    }

    @Override
    public void onSkipInWrite(Config item, Throwable t) {

        System.out.println(t);
    }

    @Override
    public void onSkipInProcess(Config item, Throwable t) {
        System.out.println(t);

    }
}
