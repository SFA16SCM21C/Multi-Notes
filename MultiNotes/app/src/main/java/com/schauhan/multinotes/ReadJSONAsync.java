package com.schauhan.multinotes;


import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ReadJSONAsync extends AsyncTask<Context, Void, String> {
        //public AsyncResponse delegate = null;

        //public ReadJSONAsync(AsyncResponse delegate){
        //   this.delegate = delegate;
        //}
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            //delegate.storedJSON(result);
            return;
        }

        @Override
        protected String doInBackground(Context... params) {
            String json = null;
            Context context = params[0];
            try {
                FileInputStream in = context.openFileInput(context.getString(R.string.file_name));
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                json = sb.toString();
            }
            catch (Exception ex)
            {

            }

            return json;
        }




}
