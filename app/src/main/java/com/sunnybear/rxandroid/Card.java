package com.sunnybear.rxandroid;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenkai.gu on 2016/11/10.
 */
public class Card implements Serializable {
    /**
     * key : m42_cname
     * name : 中文名称
     * value : ["外滩"]
     * format : ["外滩"]
     */

    private String key;
    private String name;
    private List<String> value;
    private List<String> format;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

    public List<String> getFormat() {
        return format;
    }

    public void setFormat(List<String> format) {
        this.format = format;
    }
}
