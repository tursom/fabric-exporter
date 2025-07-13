package live.noumifuurinn.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class PathFileSize {
    final private Path path;

    public PathFileSize(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("Path must not be null!");
        }
        this.path = path;
    }

    public long getSize() {
        try {
            final AtomicLong size = new AtomicLong(0);
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    size.addAndGet(attrs.size());
                    return FileVisitResult.CONTINUE;
                }

                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
            return size.get();
        } catch (IOException e) {
            log.warn("failed to get size of path: {}", path, e);
            return -1;
        }
    }
}
