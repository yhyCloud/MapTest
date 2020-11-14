package com.example.maptest;

public class FileInfo {
    private String FileName;//文件名
    private String FileSize;//文件大小
    private String generateDate;//拍摄日期
    private String generateTime;//拍摄时间

    public FileInfo(String fileName, String fileSize, String generateDate, String generateTime) {
        FileName = fileName;
        FileSize = fileSize;
        this.generateDate = generateDate;
        this.generateTime = generateTime;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getFileSize() {
        return FileSize;
    }

    public void setFileSize(String fileSize) {
        FileSize = fileSize;
    }

    public String getGenerateDate() {
        return generateDate;
    }

    public void setGenerateDate(String generateDate) {
        this.generateDate = generateDate;
    }

    public String getGenerateTime() {
        return generateTime;
    }

    public void setGenerateTime(String generateTime) {
        this.generateTime = generateTime;
    }
}
