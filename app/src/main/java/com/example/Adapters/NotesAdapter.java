package com.example.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Listeners.NotesListener;
import com.example.entities.Note;
import com.example.noteapp.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesAdapterViewHolder> {

    ArrayList<Note> notes = new ArrayList<>();
    NotesListener notesListener ;
    Context context ;

    public NotesAdapter(ArrayList<Note> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener = notesListener;
    }

    @NonNull
    @Override
    public NotesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesAdapterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapterViewHolder holder, final int position) {
        holder.textTitle.setText(notes.get(position).getTitle());
        holder.textSubtitle.setText(notes.get(position).getSubtitle());
        holder.textDateTime.setText(notes.get(position).getDateTime());
        holder.layoutNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesListener.onNoteClicked(notes.get(position),position);

            }
        });





    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class NotesAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView textTitle , textSubtitle , textDateTime ;
        LinearLayout layoutNotes ;

        RoundedImageView imageNote ;



        public NotesAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle=itemView.findViewById(R.id.textTitle);
            textSubtitle=itemView.findViewById(R.id.textSubtitle);
            textDateTime=itemView.findViewById(R.id.textDateTime);
            layoutNotes=itemView.findViewById(R.id.layoutNote);
            imageNote=itemView.findViewById(R.id.imageNote);
        }

        void setNote(Note note){
            textTitle.setText(note.getTitle());
            if (note.getSubtitle().trim().isEmpty()){
                textSubtitle.setVisibility(View.GONE);
            }else{
                textSubtitle.setText(note.getSubtitle());

            }
            textDateTime.setText(note.getDateTime());

            GradientDrawable gradientDrawable = (GradientDrawable) layoutNotes.getBackground();
            if (note.getColor() != null){
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            }else {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }

            if (note.getImagePath() != null){
                imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
                imageNote.setVisibility(View.VISIBLE);
                Toast.makeText(context, "here", Toast.LENGTH_SHORT).show();
            }else {
                imageNote.setVisibility(View.GONE);


            }

        }
    }


}
