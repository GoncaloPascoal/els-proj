package pt.up.fe.els2022.adapters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.w3c.dom.Node;

import pt.up.fe.els2022.model.Table;
import pt.up.fe.specs.util.xml.XmlDocument;
import pt.up.fe.specs.util.xml.XmlElement;
import pt.up.fe.specs.util.xml.XmlNode;

public class XmlAdapter extends StructuredAdapter {
    public XmlAdapter(String path, List<String> columns) {
        super(path, columns);
    }

    @Override
    public Table extractTable(List<File> files) {
        Table table = new Table();
        Map<String, List<String>> rows = new ListOrderedMap<>();

        XmlDocument document;
        XmlElement element;

        for (File file : files) {
            document = XmlDocument.newInstance(file);
            element = document.getElementByName(path);

            if (columns.isEmpty()) {
                // Get all nodes under element
                for (XmlNode child : element.getChildren().stream()
                        .filter(n -> n.getNode().getNodeType() == Node.ELEMENT_NODE)
                        .collect(Collectors.toList())) {
                    Node childNode = child.getNode();
                    String nodeName = childNode.getNodeName();
                    rows.putIfAbsent(nodeName, new ArrayList<>());
                    rows.get(nodeName).add(childNode.getTextContent());
                }
            }
            else {
                // Get specific nodes in a particular order
                for (String column : columns) {
                    Node childNode = element.getElementByName(column).getNode();
                    String nodeName = childNode.getNodeName();
                    rows.putIfAbsent(nodeName, new ArrayList<>());
                    rows.get(nodeName).add(childNode.getTextContent());
                }
            }
        }

        table.addRows(rows);
        return table;
    }
}
