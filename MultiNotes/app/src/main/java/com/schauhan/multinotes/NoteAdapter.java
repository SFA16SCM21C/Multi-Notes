package com.schauhan.multinotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private TreeMap<Long, Note> notesList;
    private static  NoteAdapter adapter;
    public NoteAdapter(TreeMap<Long, Note> notesList)
    {
        this.notesList = notesList;
        this.adapter = this;
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    @Override
    public void onBindViewHolder(NoteViewHolder noteViewHolder, int i) {
        Note note = (Note)notesList.values().toArray()[i];
        noteViewHolder.noteTitle.setText(note.getTitle());
        if(note.getContent().length() >= 80)
        {
            String ncontent = note.getContent().substring(0, 80) + "...";
            noteViewHolder.noteContent.setText(ncontent);
        }
        else {
            noteViewHolder.noteContent.setText(note.getContent());
        }
        SimpleDateFormat formatWrite = new SimpleDateFormat("EE MMM dd, hh:mm a");
        noteViewHolder.noteModifiedDate.setText(formatWrite.format(note.getModifieddate()));
        noteViewHolder.noteID.setText(note.getId());
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.layout_cardview, viewGroup, false);
        return new NoteViewHolder(itemView);
    }

    public void removeItem() {
        notifyDataSetChanged();
    }



    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        protected TextView noteTitle;
        protected TextView noteContent;
        protected TextView noteModifiedDate;
        protected TextView noteID;
        private View viewobj;

        public NoteViewHolder(final View view) {
            super(view);
            this.viewobj = view;
            noteTitle =  (TextView) view.findViewById(R.id.card_noteTitle);
            noteContent = (TextView)  view.findViewById(R.id.card_contentPreview);
            noteModifiedDate = (TextView)  view.findViewById(R.id.card_noteDate);
            noteID = (TextView) view.findViewById(R.id.card_id);

            view.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Intent intent = new Intent(viewobj.getContext(), activity_note.class);
                    intent.putExtra("noteID", noteID.getText());
                    intent.putExtra("noteTitle", noteTitle.getText());
                    intent.putExtra("noteContent", noteContent.getText());
                    intent.putExtra("noteModifiedDate", noteModifiedDate.getText());
                    intent.putExtra("action", "edit");
                    viewobj.getContext().startActivity(intent);
                }
            });


            view.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override public boolean onLongClick(View v)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                    builder.setTitle("Delete note");
                    builder.setMessage("Are you sure, you want to delete the note?");

                    //Delete the Note when Yes
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            //Read JSON file

                            String exisitngJSON = onResult();
                            String noteIDDel = noteID.getText().toString();

                            try{

                                //Find and remove Note
                                JSONObject jsonObject = new JSONObject(exisitngJSON);
                                JSONArray notesRoot = jsonObject.getJSONArray("notes");
                                for(int i = 0; i < notesRoot.length(); i++)
                                {
                                    JSONObject noteObj = notesRoot.getJSONObject(i);
                                    if(noteObj.getString("id").equalsIgnoreCase(noteIDDel))
                                    {
                                        notesRoot.remove(i);
                                        break;
                                    }
                                }


                                //Update JSON
                                jsonObject.put("notes", notesRoot);
                                Gson gson = new Gson();
                                FileOutputStream outputStream;
                                outputStream = viewobj.getContext().openFileOutput(viewobj.getContext().getString(R.string.file_name), Context.MODE_PRIVATE);
                                outputStream.write(jsonObject.toString().getBytes());
                                outputStream.close();

                                //Reload MainActivity
                                Intent intent = new Intent(viewobj.getContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                viewobj.getContext().startActivity(intent);

                            }
                            catch (Exception ex)
                            {

                            }

                        }

                    });

                    //Do Nothing When No
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //TODO
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    return true;
                }

                public String onResult()
                {
                    Context[] asyncTaskParams = {viewobj.getContext()};
                    //read existing JSON file Async
                    ReadJSONAsync readJSONAsync = new ReadJSONAsync();
                    String existingJSON = null;

                    try {
                        existingJSON = readJSONAsync.execute(asyncTaskParams).get();
                    } catch (Exception ex) {

                    }
                    return existingJSON;
                }

            });
        }


    }
}
