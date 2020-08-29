package com.example.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Database.NoteDatabase;
import com.example.entities.Note;
import com.example.noteapp.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {

   private ImageView imageBace , imageSave ;
   private EditText inputNoteTitle , inputNoteSubtitle , inputNoteText ;
   private TextView textDateTime ;
   private View viewSubtitleIndicator ;
   private String selectedNoteColor ;
   private static final int REQUEST_CODE_STORAGE_PERMISSION = 1 ;
   private static final int REQUEST_CODE_SELECT_IMAGE =2 ;
   private ImageView imageNote ;
   private String selectedImagePath ;
   private TextView textWebUrl ;
   private LinearLayout layoutWebUrl ;

   private AlertDialog aleartAddurl ;

   private Note aleadyAvailabeNote ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        initialVariable();
        imageBackListener();
        imagesaveListener();
        iniMiscellaneous();
        setSubtitleIndecatorColor();
        NoteDate();

        if (getIntent().getBooleanExtra("isViewOrUpdat",false)){
            aleadyAvailabeNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdate();
        }
    }




    private void initialVariable() {
        imageBace=findViewById(R.id.imageBack);
        imageSave=findViewById(R.id.imageSave);
        inputNoteTitle=findViewById(R.id.inputNoteTittle);
        inputNoteSubtitle=findViewById(R.id.inputNoteSubtitle);
        inputNoteText=findViewById(R.id.inputNote);
        textDateTime=findViewById(R.id.textDateTime);
        selectedNoteColor="#333333";
        viewSubtitleIndicator= findViewById(R.id.viewSubtitleIndecator);
        imageNote = findViewById(R.id.imageNote);
        selectedImagePath="";
        textWebUrl = findViewById(R.id.textWebUrl);
        layoutWebUrl=findViewById(R.id.layoutWebURL);

    }


    private void imageBackListener() {
        imageBace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    private void NoteDate() {
        textDateTime.setText(
                new SimpleDateFormat("EEEE,dd  MMMM YYYY HH:mm a", Locale.getDefault())
                .format(new Date())

        );
    }

    private void imagesaveListener() {
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });
    }

    private void setViewOrUpdate(){
        inputNoteTitle.setText(aleadyAvailabeNote.getTitle());
        inputNoteSubtitle.setText(aleadyAvailabeNote.getSubtitle());
        inputNoteText.setText(aleadyAvailabeNote.getNoteText());
        textDateTime.setText(aleadyAvailabeNote.getDateTime());
        if (aleadyAvailabeNote.getImagePath() != null && !aleadyAvailabeNote.getImagePath().trim().isEmpty()){
            imageNote.setImageBitmap(BitmapFactory.decodeFile(aleadyAvailabeNote.getImagePath()));
            imageNote.setVisibility(View.VISIBLE);
            selectedImagePath=aleadyAvailabeNote.getImagePath();
        }
        if (aleadyAvailabeNote.getWebLink() != null && !aleadyAvailabeNote.getWebLink().trim().isEmpty()){
            textWebUrl.setText(aleadyAvailabeNote.getWebLink());
            layoutWebUrl.setVisibility(View.VISIBLE);
        }

    }

    private void saveNote(){
        if (inputNoteTitle.getText().toString().trim().isEmpty()){
            inputNoteTitle.setError("Note Title can't be empty");
            return;
        }else
            if (inputNoteSubtitle.getText().toString().trim().isEmpty()&&inputNoteText.getText().toString().isEmpty()){
                inputNoteSubtitle.setError("Note Can't be empty");
            }

            final Note note = new Note();
            note.setTitle(inputNoteTitle.getText().toString());
            note.setSubtitle(inputNoteSubtitle.getText().toString());
            note.setNoteText(inputNoteText.getText().toString());
            note.setDateTime(textDateTime.getText().toString());
            note.setColor(selectedNoteColor);
            note.setImagePath(selectedImagePath);

            if (layoutWebUrl.getVisibility() == View.VISIBLE){
                note.setWebLink(textWebUrl.getText().toString());
            }

            if (aleadyAvailabeNote != null){
                note.setId(aleadyAvailabeNote.getId());
            }


            //room not use main thread to save so  use asynctask

            class SaveNoteTask extends AsyncTask<Void,Void,Void>{

                @Override
                protected Void doInBackground(Void... voids) {
                    NoteDatabase.getDataBase(getApplicationContext()).noteDao().insertNote(note);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    Intent intent = new Intent();
                    setResult(RESULT_OK,intent);
                    finish();

                }
            }
            new SaveNoteTask().execute();


    }

    private void iniMiscellaneous(){

        final LinearLayout layoutMiscellaneous =findViewById(R.id.layoutMiscellaneous);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textmiscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

            }
        });

        final ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imageColor5);

        layoutMiscellaneous.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor="#333333";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndecatorColor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor="#fdbf3b";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndecatorColor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor="#ff4842";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndecatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor="#3a52fc";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                setSubtitleIndecatorColor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor="#000000";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                setSubtitleIndecatorColor();
            }
        });

        if (aleadyAvailabeNote != null && aleadyAvailabeNote.getColor() != null && aleadyAvailabeNote.getColor().trim().isEmpty()){
            switch (aleadyAvailabeNote.getColor()){
                case "#fdbf3b":
                    layoutMiscellaneous.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#ff4842" :
                    layoutMiscellaneous.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#3a52fc" :
                    layoutMiscellaneous.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#000000" :
                    layoutMiscellaneous.findViewById(R.id.viewColor5).performClick();
                    break;


            }
        }

        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                )!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(CreateNoteActivity.this,
                            new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_STORAGE_PERMISSION);
                }else {
                    selectImage();
                }

            }
        });
        layoutMiscellaneous.findViewById(R.id.layoutAddUri).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showAddUrlDialog();

            }
        });


    }

    private void selectImage(){

        PackageManager pm = getPackageManager();
        String pn = getPackageName();
        PackageInfo pi = null;
        try {
            pi=pm.getPackageInfo(pn,0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(pm) != null){
            startActivityForResult(intent , REQUEST_CODE_SELECT_IMAGE);
        }

    }

    private void setSubtitleIndecatorColor(){
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode ==REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK)){
            if (data != null){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);

                        selectedImagePath=getPathFromUri(selectedImageUri);
                    } catch (FileNotFoundException e) {

                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }
    }

    private String getPathFromUri (Uri contentUri){
        String filePath ;
        Cursor cursor = getContentResolver()
                .query(contentUri ,null , null,null,null);
        if (cursor == null){
            filePath=contentUri.getPath();
        }
        else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath=cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    private void showAddUrlDialog(){
        if (aleartAddurl ==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_add_url ,
                    (ViewGroup) findViewById(R.id.layoutAddUriContainer));

            builder.setView(view);
            aleartAddurl = builder.create();

            if (aleartAddurl.getWindow() != null){
                aleartAddurl.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputUrl = view.findViewById(R.id.inputUrl);
            inputUrl.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (inputUrl.getText().toString().trim().isEmpty()){
                        inputUrl.setError("Enter Url");
                        // here is eror
                    }else
                        if (!Patterns.WEB_URL.matcher(inputUrl.getText().toString()).matches()){
                            Toast.makeText(CreateNoteActivity.this, "Enter Valid URL", Toast.LENGTH_SHORT).show();
                        }else {
                            textWebUrl.setText(inputUrl.getText().toString());
                            layoutWebUrl.setVisibility(View.VISIBLE);
                            aleartAddurl.dismiss();

                        }

                }
            });

            view.findViewById(R.id.textcancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aleartAddurl.dismiss();
                }
            });

        }
        aleartAddurl.show();
    }
}
