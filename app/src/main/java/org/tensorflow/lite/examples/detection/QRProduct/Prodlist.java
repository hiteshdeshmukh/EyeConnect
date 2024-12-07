package org.tensorflow.lite.examples.detection.QRProduct;

public class Prodlist {

    // variables for our coursename,
    // description, tracks and duration, id.
    private String prodname;
    private String price;

    private String getNum(){
        return price;
    }

    // creating getter and setter methods
    public String getCourseName() {
        return prodname;
    }
    public  void setNum(String num){
        this.price = num;
    }

    public void setCourseName(String prodname) {
        this.prodname = prodname;
    }



    // constructor
    public Prodlist(String prodname, String num) {
        this.prodname = prodname;
        this.price = num;
    }

}
