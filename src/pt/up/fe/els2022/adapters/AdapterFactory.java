package pt.up.fe.els2022.adapters;

import java.io.File;
import java.io.FileNotFoundException;

import pt.up.fe.els2022.utils.FileUtils;
import pt.up.fe.els2022.utils.UnsupportedFileExtensionException;

public final class AdapterFactory {
    private AdapterFactory() {}

    public static Adapter createAdapter(AdapterConfiguration configuration, File file) throws FileNotFoundException,
            UnsupportedFileExtensionException {
        if (!(file.exists() && file.canRead())) {
            throw new FileNotFoundException();
        }

        String extension = FileUtils.getExtension(file.getName());

        switch (extension) {
            case "xml":
                return new XmlAdapter(configuration, file);
        }

        throw new UnsupportedFileExtensionException(extension);
    }

    public static Adapter createAdapter(AdapterConfiguration configuration, String path) throws FileNotFoundException,
            UnsupportedFileExtensionException {
        return createAdapter(configuration, new File(path));
    }
}
