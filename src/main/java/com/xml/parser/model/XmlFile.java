package com.xml.parser.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "test_xml_bp")
public class XmlFile {
    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "xml")
    private byte[] xmlObject;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getXmlObject() {
        return xmlObject;
    }

    public void setXmlObject(byte[] xmlObject) {
        this.xmlObject = xmlObject;
    }
}
