package com.nekozouneko.nekojosen.util;

import com.google.common.base.Preconditions;
import org.bukkit.World;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class WorldCopyVisitor extends SimpleFileVisitor<Path> {

    private final Path wf;
    private final Path to;

    public WorldCopyVisitor(World w, Path to) {
        Preconditions.checkArgument(w != null, "World is null.");

        this.wf = w.getWorldFolder().toPath();
        this.to = to;
    }

    public WorldCopyVisitor(Path wf, Path to) {
        this.wf = wf;
        this.to = to;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        Path toDir = to.resolve(wf.relativize(dir));
        if (!Files.exists(toDir)) {
            Files.createDirectory(toDir);
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        String name = file.getFileName().toString();
        if (name.equals("session.lock") || name.equals("uid.dat")) {
            return FileVisitResult.CONTINUE;
        }

        Path toFile = to.resolve(wf.relativize(file));
        Files.copy(file, toFile, StandardCopyOption.COPY_ATTRIBUTES);

        return FileVisitResult.CONTINUE;
    }
}
