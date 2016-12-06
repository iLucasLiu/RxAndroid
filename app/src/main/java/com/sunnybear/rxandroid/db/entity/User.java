package com.sunnybear.rxandroid.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.Serializable;

/**
 * Created by chenkai.gu on 2016/12/6.
 */
@Entity(nameInDb = "RxAndroid_User")
public class User implements Serializable {
    @Id(autoincrement = true)
    private Long id;
    @Property
    private String name;
    @Property
    private String birthday;
    @Property
    private String nickName;
    @Property
    private String address;
    @Property
    private String mobilePhone;

    @Generated(hash = 1082129510)
    public User(Long id, String name, String birthday, String nickName,
            String address, String mobilePhone) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.nickName = nickName;
        this.address = address;
        this.mobilePhone = mobilePhone;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", birthday='" + birthday + '\'' +
                ", nickName='" + nickName + '\'' +
                ", address='" + address + '\'' +
                ", mobliePhone='" + mobilePhone + '\'' +
                '}';
    }
}
