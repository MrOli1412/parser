package com.xml.parser.step;

import com.xml.parser.model.Config;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class FileWriter implements ItemWriter<Config> {

    @Override
    public void write(List<? extends Config> list) throws Exception {

    }
}
