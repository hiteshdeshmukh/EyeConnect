package org.tensorflow.lite.examples.detection.Note;

//model class is used to set and get the data from the database
public class Model {
    String title, message, date,notenum;
    public Model() {
    }
    public Model(String title, String message, String date,String notenum) {
        this.title = title;
         this.message = message;
        this.date =date;
        this.notenum = notenum;
    }
    public String getTitle() {
        return title;
    }
    public String getNotenum() {
        return notenum;
    }
    public String setNotenum() {
        return notenum;
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
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
 }
