package uk.me.feixie.shoppinglist.model;

/**
 * Created by Fei on 16/12/2015.
 */
public class User {

    private int id;
    private String name;
    private String notice;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", notice='" + notice + '\'' +
                '}';
    }
}
