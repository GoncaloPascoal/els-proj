package pt.up.fe.els2022.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class ListFileVisitor extends SimpleFileVisitor<Path> {
    private final List<File> files;
    private final PathMatcher matcher;

    public ListFileVisitor(List<File> files, PathMatcher matcher) {
        this.files = files;
        this.matcher = matcher;
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
        if (matcher.matches(path)) {
            files.add(path.toFile());
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path path, IOException ex) throws IOException {
        return FileVisitResult.CONTINUE;
    }
}
