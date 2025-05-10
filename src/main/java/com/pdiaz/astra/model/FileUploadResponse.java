package com.pdiaz.astra.model;

import java.util.List;

public class FileUploadResponse {
    private String fileName;
    private String fileType;
    private long size;
    private List<FileSegment> segments;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public List<FileSegment> getSegments() {
        return segments;
    }

    public void setSegments(List<FileSegment> segments) {
        this.segments = segments;
    }
}