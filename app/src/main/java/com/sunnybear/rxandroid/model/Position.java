package com.sunnybear.rxandroid.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenkai.gu on 2016/11/27.
 */
public class Position implements Serializable {
    private String content;
    private List<String> positions;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getPositions() {
        return positions;
    }

    public void setPositions(List<String> positions) {
        this.positions = positions;
    }

    @Override
    public String toString() {
        return "Position{" +
                "content='" + content + '\'' +
                ", positions=" + positions +
                '}';
    }
}
