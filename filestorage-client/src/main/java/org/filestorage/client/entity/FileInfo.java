package org.filestorage.client.entity;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class FileInfo {
  private String name;
  private long size;
  private LocalDateTime lastModified;

  public String getName() {
    return name;
  }

  public long getSize() {
    return size;
  }

  public LocalDateTime getLastModified() {
    return lastModified;
  }

  public FileInfo(Path path) {
    try {
      this.name = path.getFileName().toString();
      if (Files.isDirectory(path)) {
        this.size = -1L;
      } else {
        this.size = Files.size(path);
      }
      this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(0));
    } catch (IOException e) {
      throw new RuntimeException("Unable to create file info by path", e);
    }
  }
}
