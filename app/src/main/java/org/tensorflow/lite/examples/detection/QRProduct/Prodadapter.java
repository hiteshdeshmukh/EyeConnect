package org.tensorflow.lite.examples.detection.QRProduct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.tensorflow.lite.examples.detection.DBHandler;
import org.tensorflow.lite.examples.detection.R;

import java.util.ArrayList;

public class Prodadapter extends RecyclerView.Adapter<Prodadapter.ViewHolder> {

    // variable for our array list and context
    private static ArrayList<Prodlist> prodlistArrayList;
    private static Context context;
    public static String result;
    // constructor
    public Prodadapter(ArrayList<Prodlist> courseModalArrayList, Context context) {
        this.prodlistArrayList = courseModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // on below line we are inflating our layout
        // file for our recycler view items.

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_rv_item, parent, false);
         return new ViewHolder(view);


    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // on below line we are setting data
        // to our views of recycler view item.

            Prodlist modal = prodlistArrayList.get(position);
            holder.prodname.setText(modal.getCourseName());
            holder.price.setText(DBHandler.arrayList.toString().replaceAll("[^a-zA-Z0-9]", " "));


    }


    public static String getvalue(){
        return result;
    }
    @Override
    public int getItemCount() {
        // returning the size of our array list
        return prodlistArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        // creating variables for our text views.
        private TextView prodname;
        private TextView price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views

            prodname = itemView.findViewById(R.id.idTVCourseName);
            price = itemView.findViewById(R.id.price);

        }}

}
