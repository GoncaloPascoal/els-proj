package pt.up.fe.els2022.adapters;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.w3c.dom.Node;

import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.Table;
import pt.up.fe.specs.util.xml.XmlDocument;
import pt.up.fe.specs.util.xml.XmlElement;
import pt.up.fe.specs.util.xml.XmlNode;

public class XmlAdapter extends Adapter {
    public XmlAdapter(File file) {
        super(file);
    }

    @Override
    public Table extractTable(String key, List<String> columns, Map<String, MetadataType> metadataColumns) {
        Table metadata = super.extractTable(key, columns, metadataColumns);

        Table table = new Table();
        Map<String, String> row = new LinkedHashMap<>();

        XmlDocument document = XmlDocument.newInstance(file);
        XmlElement element = document.getElementByName(key);

        if (columns.isEmpty()) {
            // Get all nodes under element
            for (XmlNode child : element.getChildren().stream()
                    .filter(n -> n.getNode().getNodeType() == Node.ELEMENT_NODE)
                    .collect(Collectors.toList())) {
                Node childNode = child.getNode();
                row.put(childNode.getNodeName(), childNode.getTextContent());
            }
        }
        else {
            // Get specific nodes in a particular order
            for (String column : columns) {
                Node childNode = element.getElementByName(column).getNode();
                row.put(childNode.getNodeName(), childNode.getTextContent());
            }
        }

        table.addRow(row);
        metadata.merge(table);

        return metadata;
    }
}
