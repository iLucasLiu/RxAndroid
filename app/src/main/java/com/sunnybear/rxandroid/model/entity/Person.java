package com.sunnybear.rxandroid.model.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenkai.gu on 2016/11/23.
 */
public class Person implements Serializable {
    private String name;
    private String age;
    private List<String> mobiles;

    public Person(String name, String age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public List<String> getMobiles() {
        return mobiles;
    }

    public void setMobiles(List<String> mobiles) {
        this.mobiles = mobiles;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", mobiles=" + mobiles +
                '}';
    }
}
