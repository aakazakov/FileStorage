package org.filestorage.client;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class FileInfo {
  public enum Type {
    FILE("F"), DIRECTORY("D");
  
    private String name;
  
    public String getName() {
      return this.name;
    }
  
    Type(String name) {
      this.name = name;
    }
  }

  private String name;
  private Type type;
  private long size;
  private LocalDateTime lastModified;

  public String getName() {
    return name;
  }

  public Type getType() {
    return type;
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
        this.type = Type.DIRECTORY;
        this.size = -1L;
      } else {
        this.type = Type.FILE;
        this.size = Files.size(path);
      }
      this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(0));
    } catch (IOException e) {
      throw new RuntimeException("Unable to create file info by path", e);
    }
  }
}
