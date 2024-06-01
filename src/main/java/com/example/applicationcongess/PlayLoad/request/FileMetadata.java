package com.example.applicationcongess.PlayLoad.request;

public class FileMetadata {
    private long imageId;
    private String fileName;
    private String contentType;
    private long size;

    // Constructeur, getters et setters
    public FileMetadata(long imageId, String fileName, String contentType, long size) {
        this.imageId = imageId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
    }

    // Getters et setters
    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
