package org.filestorage.common.entity;

import java.io.Serializable;
import java.util.List;

public class FileList implements Serializable {
  private static final long serialVersionUID = -6087665956431180818L;
  
  private List<FileInfo> list;
  private String root;

  public List<FileInfo> getList() {
    return list;
  }

  public void setList(List<FileInfo> list) {
    this.list = list;
  }
  
  public String getRoot() {
    return root != null ? root : "...";
  }
  
  public FileList(String root) {
    this.root = root;
  }
  
  public FileList() { }
}
