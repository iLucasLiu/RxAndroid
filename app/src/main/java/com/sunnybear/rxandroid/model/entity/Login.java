package com.sunnybear.rxandroid.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 登录
 * Created by chenkai.gu on 2016/10/11.
 */
public class Login implements Serializable {
    private String loginResult;
    private User user;
    private List<News> newList;

    public String getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(String loginResult) {
        this.loginResult = loginResult;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<News> getNewList() {
        return newList;
    }

    public void setNewList(List<News> newList) {
        this.newList = newList;
    }

    @Override
    public String toString() {
        return "Login{" +
                "loginResult='" + loginResult + '\'' +
                ", user=" + user +
                ", newList=" + newList +
                '}';
    }
}
