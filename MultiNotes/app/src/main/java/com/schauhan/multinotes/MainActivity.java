package com.schauhan.multinotes;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.TreeMap;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);

        //Set vertical scrolling to the Recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        String existingJSON = onResult();
        TreeMap<Long, Note> notesList = createNotesList(existingJSON);

        NoteAdapter noteAdapter = new NoteAdapter(notesList);
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //create menu on action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_note:
                //directing to new note page
                Intent newNote = new Intent(this, activity_note.class);
                newNote.putExtra("action", "new");
                startActivityForResult(newNote, 1);
                break;
            case R.id.action_about:
                //direct to about us page
                startActivity(new Intent(this, activity_about.class));
                break;

        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Load JSON File


        super.onActivityResult(requestCode, resultCode, data);

        if(data != null && resultCode == Activity.RESULT_CANCELED && data.hasExtra("note_name"))
        {
            String note_name = data.getExtras().getString("note_name");
            if(note_name.isEmpty() || note_name == null)
            {
                Toast.makeText(MainActivity.this, "Your Un-titled note was not saved", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(MainActivity.this, "Your note named " + note_name + " was not saved", Toast.LENGTH_LONG).show();
            }

        }


        String existingJSON = onResult();
        TreeMap<Long, Note> notesList = createNotesList(existingJSON);

        NoteAdapter noteAdapter = new NoteAdapter(notesList);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setAdapter(noteAdapter);

    }

    public String onResult() {
        Context[] asyncTaskParams = {this};
        //read existing JSON file Async
        ReadJSONAsync readJSONAsync = new ReadJSONAsync();
        String existingJSON = null;

        try {
            existingJSON = readJSONAsync.execute(asyncTaskParams).get();
        } catch (Exception ex) {

        }
        return existingJSON;
    }

    public TreeMap<Long, Note> createNotesList(String exisitngJSON) {
        TreeMap<Long, Note > sortedTree = new TreeMap<Long, Note>(Collections.<Long>reverseOrder());

        JSONObject rootJSON = null;

        try {
            rootJSON = new JSONObject(exisitngJSON);
            JSONArray notesArray = rootJSON.getJSONArray("notes");
            for (int i = 0; i < notesArray.length(); i++) {
                JSONObject noteObject = notesArray.getJSONObject(i);
                String id = noteObject.getString("id");
                String title = noteObject.getString("title");
                String content = noteObject.getString("content");
                String createddate = noteObject.getString("createddate");
                String modifieddate = noteObject.getString("modifieddate");


                DateFormat formatRead = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");


                Date dateRead_modified = formatRead.parse(modifieddate);
                Date dateRead_created = formatRead.parse(createddate);

                Note noteIter = new Note(id, title, content, dateRead_created, dateRead_modified);
                sortedTree.put(dateRead_modified.getTime(), noteIter);
            }
        }
        catch (Exception ex) {
            String a = "dsad";
        }

        return sortedTree;
    }


    public void refreshView(JSONObject jsonObject)
    {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.setHasFixedSize(true);

        //Set vertical scrolling to the Recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        NoteAdapter noteAdapter = new NoteAdapter(createNotesList(jsonObject.toString()));

        recyclerView.setAdapter(noteAdapter);
        noteAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause(){

        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        String existingJSON = onResult();
        TreeMap<Long, Note> notesList = createNotesList(existingJSON);

        NoteAdapter noteAdapter = new NoteAdapter(notesList);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardList);
        recyclerView.invalidate();
        recyclerView.setAdapter(noteAdapter);
    }

}
