package id.mainski.asuran;


import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.Thread;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ChatActivity extends AppCompatActivity {

    ListView listview;
    EditText chat_text;
    Button SEND;
    boolean position = false;
    ChatAdapter adapter;
    Context ctx = this;
    int i = 1;

    String[] bot = new String[20];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        listview = (ListView) findViewById(R.id.chat_list_view);
        chat_text = (EditText) findViewById(R.id.chatTxt);
        SEND = (Button) findViewById(R.id.send_button);
        adapter = new ChatAdapter(ctx,R.layout.single_message_layout);
        listview.setAdapter(adapter);
        listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

        Intent caller = getIntent();
        String userUsername = caller.getStringExtra("nbUSername");
        String userFirstName = caller.getStringExtra("nbFirstName");
        String userLastName = caller.getStringExtra("nbLastName");

        bot[0] = "How are you, "+ userFirstName +"?";
        bot[1] = "You know when/where you'll need our insurance?";
        bot[2] = "First, can you tell us your full address?";
        bot[3] = "What are your occupation currently?";
        bot[4] = "What is your hobby?";
        bot[5] = "We've compiled the best packet when & where you'll need each insurance.";

        adapter.add(new DataProvider(position, bot[0]));
        position = !position;

        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listview.setSelection(adapter.getCount()-1);
            }
        });
        SEND.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i==5){
                    adapter.add(new DataProvider(position, chat_text.getText().toString()));
                    position = !position;
                    chat_text.setText("");
                    adapter.add(new DataProvider(position, bot[i]));
                    position = !position;

                    /*Intent goToCheck = new Intent(ChatActivity.this, MainActivity.class);
                    ChatActivity.this.startActivity(goToCheck);
                    finish();*/

                    new ChatActivity.DataChatTask().execute("http://104.154.82.27:8080/calculateSummary/");
                }
                adapter.add(new DataProvider(position, chat_text.getText().toString()));
                position = !position;
                chat_text.setText("");
                adapter.add(new DataProvider(position, bot[i]));
                position = !position;
                i++;
            }
        });
    }



    public class DataChatTask extends AsyncTask<String,String,String> {

        StringBuilder sb = new StringBuilder();

        @Override
        protected String doInBackground(String... params) {

            System.out.println("asd");
            HttpURLConnection urlConnection=null;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setRequestProperty("Content-Type","application/json");

                urlConnection.connect();

                //Create JSONObject here
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("address", "cologne");
//                jsonParam.put("password", "message");
                OutputStreamWriter out = new   OutputStreamWriter(urlConnection.getOutputStream());
                out.write(jsonParam.toString());
                out.close();

                int HttpResult =urlConnection.getResponseCode();
                if(HttpResult ==HttpURLConnection.HTTP_OK){
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream(),"utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    System.out.println(""+sb.toString());

                }else{
                    System.out.println(urlConnection.getResponseMessage());
                }
            } catch (MalformedURLException e) {

                e.printStackTrace();
            }
            catch (IOException e) {

                e.printStackTrace();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }finally{
                if(urlConnection!=null)
                    urlConnection.disconnect();
            }

            // TODO: register the new account here.
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String dataCheck = sb.toString();
            Intent goToCheck = new Intent(ChatActivity.this, MainActivity2.class);
            goToCheck.putExtra("nbDataCheck", dataCheck);
            ChatActivity.this.startActivity(goToCheck);
            finish();
        }
    }
}
