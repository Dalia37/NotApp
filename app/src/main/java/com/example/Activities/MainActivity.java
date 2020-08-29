package com.example.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.Adapters.NotesAdapter;
import com.example.Database.NoteDatabase;
import com.example.Listeners.NotesListener;
import com.example.entities.Note;
import com.example.noteapp.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener {
    public static final int REQUEST_CODE_ADD_NOTE =1 ;
    public static final int REQUEST_CODE_UPDATE_NOTE =2 ;
    public static final int REQUEST_CODE_SHOW_NOTE =2 ;


    private ImageView imageAddNoteMain;
    private RecyclerView rv ;
    private ArrayList<Note> noteList ;
    private NotesAdapter adapter ;
    private int noteClickedPosition=-1  ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialeVariable();
        addNoteMainListener();
        getNotes(REQUEST_CODE_SHOW_NOTE);

        adapter = new NotesAdapter(noteList,this);
        rv.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        rv.setHasFixedSize(true);
        rv.setAdapter(adapter);

    }



    private void initialeVariable() {
        imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        rv=findViewById(R.id.noteRecyclerView);
        noteList=new ArrayList<>();

    }

    private void addNoteMainListener() {
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(),CreateNoteActivity.class),REQUEST_CODE_ADD_NOTE);
            }
        });
    }

    //so we need async task to get data from database

    private void getNotes(final  int requestCode){
        class GetNotesTask extends AsyncTask<Void,Void, List<Note>>{


            @Override
            protected List<Note> doInBackground(Void... voids) {

                return NoteDatabase.getDataBase(getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);

                if (requestCode == REQUEST_CODE_SHOW_NOTE){
                    noteList.addAll(notes);
                    adapter.notifyDataSetChanged();
                }else if (requestCode == REQUEST_CODE_ADD_NOTE){
                    noteList.add(0,notes.get(0));
                    adapter.notifyItemInserted(0);
                    rv.smoothScrollToPosition(0);
                }else if (requestCode ==REQUEST_CODE_UPDATE_NOTE){
                    noteList.remove(noteClickedPosition);
                    noteList.add(noteClickedPosition,notes.get(noteClickedPosition));
                    adapter.notifyItemChanged(noteClickedPosition);
                }

            }
        }
        new GetNotesTask().execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==REQUEST_CODE_ADD_NOTE && resultCode==RESULT_OK ){
            getNotes(REQUEST_CODE_SHOW_NOTE);
        }else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK){
            if (data != null){
                getNotes(REQUEST_CODE_UPDATE_NOTE);
            }

        }


    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position ;
        Intent intent = new Intent(getApplicationContext() , CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdat",true);
        intent.putExtra("note",note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE );



    }
}
