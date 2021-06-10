package models;

import org.json.simple.JSONArray;

public class StatementModel {
    public String stmt;
    public String imgPath;
    public String soundPath;
    public String exePath;
    public JSONArray params;

    public StatementModel(String stmt, String imgPath, String soundPath, String exePath, JSONArray params) {
        this.stmt = stmt;
        this.imgPath = imgPath.equals("null") ? null : imgPath;
        this.soundPath = soundPath.equals("null") ? null : soundPath;
        this.exePath = exePath.equals("null") ? null : exePath;
        this.params = params;
    }

}
