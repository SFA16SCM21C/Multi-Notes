package com.schauhan.multinotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class activity_note extends ActionBarActivity implements AsyncResponse {


    public static String action;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        action = null;
        setContentView(R.layout.activity_note);
        Bundle extras = getIntent().getExtras();
        EditText noteTitle = (EditText)findViewById(R.id.note_title);
        EditText noteContent = (EditText)findViewById(R.id.note_content);
        noteTitle.setText(extras.getString("noteTitle"));
        noteContent.setText(extras.getString("noteContent"));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //create menu on action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_note_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_save:
                String txtTitle = ((EditText)findViewById(R.id.note_title)).getText().toString();
                if(!txtTitle.isEmpty() && txtTitle != null)
                    finish();
                else
                    Toast.makeText(this, getString(R.string.title_empty), Toast.LENGTH_LONG).show();

        }
        return true;
    }



    private void saveNote(String txtTitle, String txtContent)
    {


        Note newNote = new Note(txtTitle, txtContent);
        String action = getIntent().getExtras().getString("action");

        File jsonFile = new File(this.getFilesDir(), getString(R.string.file_name));
        int result;

        if(!jsonFile.exists() && action.equalsIgnoreCase("new"))
        {
            createNewFile(newNote);

        } else if (jsonFile.exists() && action.equalsIgnoreCase("new"))
        {
            onResult();
        } else if (jsonFile.exists() && action.equalsIgnoreCase("edit"))
        {
            onResult();
        }
    }

    @Override
    public void onBackPressed()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity_note.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("Save Note");
        builder.setMessage("Your note is not saved, would you like to save it?");

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String txtTitle = ((EditText)findViewById(R.id.note_title)).getText().toString();

                if(txtTitle.isEmpty() || txtTitle == null)
                {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("note_name", txtTitle);
                    setResult(Activity.RESULT_CANCELED, resultIntent);
                    finish();
                }
                action = null;
                finish();

            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent resultIntent = new Intent();
                String txtTitle = ((EditText)findViewById(R.id.note_title)).getText().toString();
                resultIntent.putExtra("note_name", txtTitle);
                action = "cancel";
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        // optional depending on your needs
    }


    @Override
    protected void onPause(){

        String txtTitle = ((EditText)findViewById(R.id.note_title)).getText().toString();
        String txtContent = ((EditText)findViewById(R.id.note_content)).getText().toString();
        if(!txtTitle.isEmpty() && txtTitle != null  && action != "cancel") {
            saveNote(txtTitle, txtContent);
        }
        super.onPause();
    }

    @Override
    //This method will be called when the JSON has been readed with output as JSON
    public void storedJSON(String output){
        //appendNotesToJSON(output);
    }


    private void createNewFile(Note newNote)
    {
        Gson gson = new Gson();
        FileOutputStream outputStream;
        try {

            //Create JSON from Java Object
            JSONObject rootJSON = new JSONObject();
            JSONArray notesRoot = new JSONArray();
            notesRoot.put(new JSONObject(gson.toJson(newNote)));
            rootJSON.put("notes", notesRoot);

            //Write and Save to JSON file
            outputStream = openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
            outputStream.write(rootJSON.toString().getBytes());
            outputStream.close();

        }
        catch (Exception ex)
        {

        }

    }

    private void onResult()
    {
        Context[] asyncTaskParams = {this};
        //read existing JSON file Async
        ReadJSONAsync readJSONAsync = new ReadJSONAsync();
        String action = getIntent().getExtras().getString("action");
        String existingJSON = null;

        try {
            existingJSON = readJSONAsync.execute(asyncTaskParams).get();
            if(action.equalsIgnoreCase("new")) {
                appendNotesToJSON(existingJSON);
            }
            else if(action.equalsIgnoreCase("edit"))
            {
                editNotesToJSON(existingJSON);
            }
        }
        catch (Exception ex)
        {

        }
    }


    private void appendNotesToJSON(String existingJSON)
    {
        Gson gson = new Gson();
        FileOutputStream outputStream;
        String txtTitle = ((EditText)findViewById(R.id.note_title)).getText().toString();
        String txtContent = ((EditText)findViewById(R.id.note_content)).getText().toString();
        Note newNote = new Note(txtTitle, txtContent);
        JSONObject rootJSON = null;

        try {
            //parse existing JSON
            rootJSON = new JSONObject(existingJSON);
            JSONArray notesRoot = rootJSON.getJSONArray("notes");

            //add new note to JSON
            JSONObject newNotesObj = new JSONObject(gson.toJson(newNote));
            notesRoot.put(newNotesObj);
            rootJSON.put("notes", notesRoot);

            //update JSON
            outputStream = openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
            outputStream.write(rootJSON.toString().getBytes());
            outputStream.close();



        }
        catch (Exception ex)
        {

        }

    }


    private void editNotesToJSON(String existingJSON)
    {
        Bundle extras = getIntent().getExtras();
        String noteID = extras.getString("noteID");
        String noteTitle = ((EditText)findViewById(R.id.note_title)).getText().toString();
        String noteContent = ((EditText)findViewById(R.id.note_content)).getText().toString();

        try
        {
            JSONObject rootJSON = new JSONObject(existingJSON);
            JSONArray rootNotes = rootJSON.getJSONArray("notes");

            for(int i =0; i < rootNotes.length(); i++)
            {
                JSONObject noteJSONObj = rootNotes.getJSONObject(i);

                if(noteJSONObj.getString("id").equalsIgnoreCase(noteID))
                {
                    DateFormat formatRead = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
                    Date createddate = formatRead.parse(noteJSONObj.getString("createddate"));

                    Note noteEdited = new Note(noteID, noteTitle, noteContent, createddate, new Date());
                    Gson gson= new Gson();
                    String noteEditJSON = gson.toJson(noteEdited);

                    rootNotes.remove(i);
                    rootNotes.put(new JSONObject(noteEditJSON));
                    rootJSON.put("notes", rootNotes);



                    break;
                }
            }

            FileOutputStream outputStream = openFileOutput(getString(R.string.file_name), Context.MODE_PRIVATE);
            outputStream.write(rootJSON.toString().getBytes());
            outputStream.close();

        }
        catch (Exception ex)
        {

        }


    }



}
