package pt.up.fe.els2022.adapters;

import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.w3c.dom.Node;

import pt.up.fe.els2022.model.Table;
import pt.up.fe.specs.util.xml.XmlDocument;
import pt.up.fe.specs.util.xml.XmlElement;
import pt.up.fe.specs.util.xml.XmlNode;

public class XmlAdapter extends Adapter {
    public XmlAdapter(AdapterConfiguration configuration, File file) {
        super(configuration, file);
    }

    @Override
    public boolean acceptsConfiguration() {
        return super.acceptsConfiguration() && configuration.getKey() != null && configuration.getColumns() != null;
    }

    @Override
    public Table extractTable() {
        Table metadata = super.extractTable();

        Table table = new Table();
        Map<String, String> row = new ListOrderedMap<>();

        XmlDocument document = XmlDocument.newInstance(file);
        XmlElement element = document.getElementByName(configuration.getKey());

        if (configuration.getColumns().isEmpty()) {
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
            for (String column : configuration.getColumns()) {
                Node childNode = element.getElementByName(column).getNode();
                row.put(childNode.getNodeName(), childNode.getTextContent());
            }
        }

        table.addRow(row);
        metadata.merge(table);

        return metadata;
    }
}
