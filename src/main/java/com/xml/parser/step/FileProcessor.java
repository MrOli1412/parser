package com.xml.parser.step;

import com.xml.parser.model.Config;
import com.xml.parser.model.Element;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileProcessor implements ItemProcessor<Config, Config> {

    private String processingJobid;
    /**
     * holds theProcessing File Name
     */
    private String processingFileName;
    /**
     * @return the processingJobid
     */
    public String getProcessingJobid() {
        return processingJobid;
    }

    /**
     * @param processingJobid the processingJobid to set
     */
    public void setProcessingJobid(String processingJobid) {
        this.processingJobid = processingJobid;
    }

    /**
     * @return the processingFileName
     */
    public String getProcessingFileName() {
        return processingFileName;
    }

    /**
     * @param processingFileName the processingFileName to set
     */
    public void setProcessingFileName(String processingFileName) {
        this.processingFileName = processingFileName;
    }

    @Override
    public Config process(Config s)  {
        return s;
    }


    public Set<String> listFilesUsingJavaIO() {
        return Stream.of(new File("C://projekt/parser/src/main/resources/static").listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }
}
