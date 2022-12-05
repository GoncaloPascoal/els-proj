package pt.up.fe.els2022.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.regex.Pattern;

public class ListFileVisitor extends SimpleFileVisitor<Path> {
    private final List<File> files;
    private final Pattern pattern;

    public ListFileVisitor(List<File> files, Pattern pattern) {
        this.files = files;
        this.pattern = pattern;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        if (pattern.matcher(path.toString()).matches()) {
            files.add(path.toFile());
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path path, IOException ex) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
