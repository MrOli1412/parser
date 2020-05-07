package com.xml.parser.step;

import com.xml.parser.model.Config;
import com.xml.parser.model.Element;
import com.xml.parser.model.XmlFile;
import com.xml.parser.repository.XmlFileRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileDbWriter implements ItemWriter<Config> {
    @Autowired
    XmlFileRepository xmlFileRepository;

    @Value(value = "${files.path}")
    private String resourcesPath;

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
    public void write(List<? extends Config> list) throws Exception {
        List<Element> elements = list.get(0).getElements();
        long missing = elements.stream().filter(element -> element.getStatus().equals("MISSING")).count();
        if(missing<=0) {
            Config config = list.get(0);
            config.getElements().forEach(element -> {
                try {
                    byte[] array = Files.readAllBytes(Paths.get(resourcesPath + "\\" + element.getFileName()));
                    XmlFile xmlFile = new XmlFile();
                    xmlFile.setId(element.getId());
                    xmlFile.setName(element.getFileName());
                    xmlFile.setXmlObject(array);
                    element.setStatus("PROCESSING");
                    xmlFileRepository.save(xmlFile);
                    element.setStatus("SAVING");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });
        }
    }
}
