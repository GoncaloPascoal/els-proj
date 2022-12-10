package pt.up.fe.els2022.adapters;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pt.up.fe.els2022.model.Table;

public class XmlAdapter extends StructuredAdapter {
    public XmlAdapter(List<String> paths, List<String> columns) {
        super(paths, columns);
    }

    @Override
    public Table extractTable(List<File> files) {
        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
        XPath xPath = XPathFactory.newInstance().newXPath();
        Document document;
        Node node;

        Table table = new Table();
        Map<String, List<String>> rows = new ListOrderedMap<>();

        for (File file : files) {
            for (String path : paths) {
                try {
                    document = builder.parse(file);
                    node = (Node) xPath.compile(path).evaluate(document, XPathConstants.NODE);
                }
                catch (IOException | SAXException | XPathExpressionException ex) {
                    throw new RuntimeException(ex);
                }
    
                if (node == null) {
                    throw new RuntimeException("Node specified by path does not exist.");
                }
    
                if (columns.isEmpty()) {
                    // Get all nodes under element
                    NodeList children = node.getChildNodes();
    
                    for (int i = 0; i < children.getLength(); ++i) {
                        Node child = children.item(i);
    
                        if (child.getNodeType() == Node.ELEMENT_NODE) {
                            String name = child.getNodeName();
                            
                            if (child.getFirstChild().getNodeType() == Node.TEXT_NODE) {
                                rows.putIfAbsent(name, new ArrayList<>());
                                rows.get(name).add(child.getTextContent());
                            }
                        }
                    }
                }
                else {
                    // Get specific nodes in a particular order
                    for (String column : columns) {
                        NodeList children = node.getChildNodes();
    
                        for (int i = 0; i < children.getLength(); ++i) {
                            Node child = children.item(i);
                            
                            if (child.getNodeType() == Node.ELEMENT_NODE) {
                                String name = child.getNodeName();
    
                                if (child.getFirstChild().getNodeType() == Node.TEXT_NODE &&
                                        name.equals(column)) {
                                    rows.putIfAbsent(name, new ArrayList<>());
                                    rows.get(name).add(child.getTextContent());
                                }
                            }
                        }
                    }
                }
            }
        }

        table.addRows(rows);
        return table;
    }
}
