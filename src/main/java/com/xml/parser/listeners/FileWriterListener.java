package com.xml.parser.listeners;

import com.xml.parser.model.Config;
import org.springframework.batch.core.ItemWriteListener;

import java.util.List;

public class FileWriterListener implements ItemWriteListener<Config> {
    @Override
    public void beforeWrite(List<? extends Config> list) {

    }

    @Override
    public void afterWrite(List<? extends Config> list) {

    }

    @Override
    public void onWriteError(Exception e, List<? extends Config> list) {

    }
}
