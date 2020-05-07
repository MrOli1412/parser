package com.xml.parser.step;

import com.xml.parser.model.Config;
import com.xml.parser.model.Element;
import com.xml.parser.model.XmlFile;
import com.xml.parser.repository.XmlFileRepository;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileProcessor implements ItemProcessor<Config, Config> {
    @Value("${files.path}")
    private String resourcesPath;


    @Autowired
    XmlFileRepository xmlFileRepository;

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
    public Config process(Config s) throws IOException {
        Set<String> fileNames = listFilesUsingJavaIO();
        List<Element> elements = s.getElements();
        elements.forEach(element -> {
            if (fileNames.contains(element.getFileName())) {
                element.setStatus("IN");
            } else {
                element.setStatus("MISSING");
                throw new RuntimeException("Missing file");
            }
        });
        return s;
    }


    public Set<String> listFilesUsingJavaIO() {
        return Stream.of(new File("C://wp/task").listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

//    private List<Element> saveToDb(List<Element> elements) throws IOException {
//        List<XmlFile> xmlFiles = new ArrayList<>();
//        elements.forEach(element -> {
//            try {
//                byte[] array = Files.readAllBytes(Paths.get(resourcesPath + "\\" + element.getFileName()));
//                XmlFile xmlFile = new XmlFile();
//                xmlFile.setId(element.getId());
//                xmlFile.setName(element.getFileName());
//                xmlFile.setXmlObject(array);
//                xmlFiles.add(xmlFile);
//                element.setStatus("PROCESSING");
//                xmlFileRepository.save(xmlFile);
//                element.setStatus("SAVING");
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//
//        });
//        return elements;
//    }
}
