package com.organizer.core;

import java.util.ArrayList;
import java.util.List;

public class FileNode {
    public String name;
    public String path;
    public boolean isDirectory;
    public long size;
    public List<FileNode> children;

    public FileNode(String name, String path, boolean isDirectory, long size) {
        this.name = name;
        this.path = path;
        this.isDirectory = isDirectory;
        this.size = size;
        this.children = new ArrayList<>();
    }

    public void addChild(FileNode child) {
        this.children.add(child);
    }
}