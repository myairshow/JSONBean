package cn.airshow.jsonbean;

import cn.airshow.json.JSONBean;

/**
 * Created by liuxunlan on 16/7/21.
 */
public class Cat extends JSONBean{
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
