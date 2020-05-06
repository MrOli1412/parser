package com.xml.parser.step;

import com.xml.parser.model.Config;
import org.springframework.batch.item.ItemProcessor;

public class FileProcessor implements ItemProcessor<String, Config> {
    @Override
    public Config process(String s) throws Exception {
        return null;
    }
}
