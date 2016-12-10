package com.sunnybear.library.network.file;

import java.io.Serializable;

/**
 * Created by chenkai.gu on 2016/12/10.
 */
public class DownloadInfo implements Serializable {
    private String url;
    private String path;
    private long startPosition;
    private long endPosition;

    public DownloadInfo() {
    }

    public DownloadInfo(String url, String path, long startPosition, long endPosition) {
        this.url = url;
        this.path = path;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }

    public long getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(long endPosition) {
        this.endPosition = endPosition;
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                '}';
    }
}
