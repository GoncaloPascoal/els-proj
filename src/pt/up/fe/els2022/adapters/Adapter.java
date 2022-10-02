package pt.up.fe.els2022.adapters;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.w3c.dom.Node;

import pt.up.fe.els2022.model.MetadataType;
import pt.up.fe.els2022.model.Table;
import pt.up.fe.els2022.utils.FileUtils;
import pt.up.fe.els2022.utils.UnsupportedFileExtensionException;
import pt.up.fe.specs.util.xml.XmlDocument;
import pt.up.fe.specs.util.xml.XmlElement;
import pt.up.fe.specs.util.xml.XmlNode;

public class Adapter {
    private final String key;
    private final List<String> columns;
    private final Map<String, MetadataType> metadataColumns;

    public Adapter(String key, List<String> columns, Map<String, MetadataType> metadataColumns) {
        this.key = key;
        this.columns = columns;
        this.metadataColumns = metadataColumns;
    }

    public Table extractTable(File file) throws FileNotFoundException,
            UnsupportedFileExtensionException {
        Table metadata = extractMetadataTable(file);
        Table data = extractDataTable(file);
        metadata.merge(data);

        return metadata;
    }

    public Table extractTable(String path) throws FileNotFoundException,
            UnsupportedFileExtensionException {
        return extractTable(new File(path));
    }

    private Table extractMetadataTable(File file) {
        Table table = new Table();
        Map<String, String> row = new LinkedHashMap<>();
        metadataColumns.forEach((k, v) -> row.put(k, v.value(file)));
        table.addRow(row);
        return table;
    }

    private Table extractDataTable(File file) throws FileNotFoundException,
            UnsupportedFileExtensionException {
        if (!(file.exists() && file.canRead())) {
            throw new FileNotFoundException();
        }

        String extension = FileUtils.getExtension(file.getName());

        switch (extension) {
            case "xml":
                return extractDataTableFromXML(file);
        }

        throw new UnsupportedFileExtensionException(extension);
    }

    private Table extractDataTableFromXML(File file) {
        Table table = new Table();
        Map<String, String> row = new ListOrderedMap<>();

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
        return table;
    }
}
