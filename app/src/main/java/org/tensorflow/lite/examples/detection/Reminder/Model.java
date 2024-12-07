package org.tensorflow.lite.examples.detection.Reminder;

//model class is used to set and get the data from the database
public class Model {
    String title, date, time,Datetime;
    public Model() {
    }
    public Model(String title, String date, String time, String Datetime) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.Datetime = Datetime;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public void setDatetime(String Datetime){
        this.Datetime = Datetime;
    }
    public String getDatetime(){
       return Datetime;
    }
}
