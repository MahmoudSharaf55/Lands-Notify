package models;

public class ConnectionModel {
    public String ip;
    public String sid;
    public String username;
    public String password;

    public ConnectionModel(String ip, String sid, String username, String password) {
        this.ip = ip;
        this.sid = sid;
        this.username = username;
        this.password = password;
    }

}
