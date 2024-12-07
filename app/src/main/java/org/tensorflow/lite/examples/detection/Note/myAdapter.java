package org.tensorflow.lite.examples.detection.Note;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.tensorflow.lite.examples.detection.R;

import java.util.ArrayList;

public class myAdapter extends RecyclerView.Adapter<myAdapter.myviewholder> {
    ArrayList<Model> dataholder;                                               //array list to hold the reminders
     public myAdapter(ArrayList<Model> dataholder) {
        this.dataholder = dataholder;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_note_file, parent, false);  //inflates the xml file in recyclerview
        return new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        holder.mTitle.setText(dataholder.get(position).getTitle());                                 //Binds the single reminder objects to recycler view
        holder.date.setText(dataholder.get(position).getDate());
        holder.note.setText(dataholder.get(position).getMessage());
        holder.notenum.setText("Note id "+dataholder.get(position).getNotenum());
     }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }

    static class myviewholder extends RecyclerView.ViewHolder {

         TextView mTitle, note, date,notenum;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txtTitle);                               //holds the reference of the materials to show data in recyclerview
            note = (TextView) itemView.findViewById(R.id.txtDate);
            date = (TextView) itemView.findViewById(R.id.txtTime);
            notenum = (TextView) itemView.findViewById(R.id.notenum);
         }
    }
}
