package cn.gl.searchengine.maintest;


import java.util.concurrent.CopyOnWriteArraySet;

class Person {
    String name;
    Integer age;

    public Person(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

public class Test4 {

    public static void main(String[] args) {
        CopyOnWriteArraySet<Person> set = new CopyOnWriteArraySet<>();
        Person p1 = new Person("gl", 20);
        Person p2 = new Person("tony", 30);
        Person p3 = new Person("jack", 40);
        set.add(p1);
        set.add(p2);
        set.add(p3);
        System.out.println(set);
    }
}
