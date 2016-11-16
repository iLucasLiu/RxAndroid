package com.sunnybear.library.basic.service;

/**
 * Lifecycle events that can be emitted by Service.
 * Created by chenkai.gu on 2016/11/16.
 */
public enum ServiceEvent {
    CREATE,
    START_COMMAND,
    BIND,
    UNBIND,
    DESTROY
}
