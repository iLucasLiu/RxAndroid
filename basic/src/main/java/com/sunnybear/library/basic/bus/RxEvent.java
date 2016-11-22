package com.sunnybear.library.basic.bus;

import java.io.Serializable;

/**
 * 事件实体
 * Created by chenkai.gu on 2016/11/21.
 */
public class RxEvent<T> implements Serializable {
    public String tag;
    public T event;

    public RxEvent(String tag, T event) {
        this.tag = tag;
        this.event = event;
    }
}
