package com.gurudeveloperinc.regex;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.gurudeveloperinc.regex.Constants.courseUrl;
import static com.gurudeveloperinc.regex.Constants.getCommentsUrl;
import static com.gurudeveloperinc.regex.Constants.postCommentUrl;
import static com.gurudeveloperinc.regex.Constants.queue;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener{

    TextView lnameTV, ldeptTV,lcourseTV,chatTV,messageTV;
    ImageButton sendButton;
    String position,cid;
    int sid;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        cid = getIntent().getStringExtra("cid");
        sid = getIntent().getIntExtra("sid",0);

        lnameTV = (TextView) findViewById(R.id.lnameTextView);
        ldeptTV = (TextView) findViewById(R.id.ldeptTextView);
        lcourseTV = (TextView) findViewById(R.id.lcourseTextView);
        chatTV = (TextView) findViewById(R.id.chatTextView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        messageTV = (TextView) findViewById(R.id.messageTextView);
        messageTV.setOnClickListener(this);
        sendButton = (ImageButton) findViewById(R.id.sendMessageButton);
        sendButton.setOnClickListener(this);


        //instantiate the request queue
        queue = Volley.newRequestQueue(this);

        getCourseDetails();
        getComments();

    }

    public void getCourseDetails(){
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, courseUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {


                                JSONObject jsonResponse = null;
                                try {
                                    //parse the json response
                                    jsonResponse = new JSONObject(response);

                                    String cname = jsonResponse.getJSONObject("course").getString("name");
                                    String lname = jsonResponse.getJSONObject("lecturer").getString("name");
                                    String dept = jsonResponse.getJSONObject("dept").getString("name");

                                    lnameTV.setText(lname);
                                    lcourseTV.setText(cname);
                                    ldeptTV.setText(dept);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Snackbar.make(lcourseTV,"Sorry an error occurred. Please try again", Snackbar.LENGTH_LONG).show();

                            }
                        }){
                    @Override
                    public Map<String, String> getParams(){
                        Map<String, String> params = new HashMap<>();
                        params.put("cid" , cid );
                        return params;
                    }
                };

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }

    public void getComments(){
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, getCommentsUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            String chatData = "";
                            JSONObject jsonResponse = null;
                            try {
                                //parse the json response
                                jsonResponse = new JSONObject(response);

                                JSONArray timeline = jsonResponse.getJSONArray("timeline");

                                for(int i=0; i<timeline.length(); i++){
                                    if(timeline.getJSONObject(i).getString("fromLecturer").equals("0")){
                                        chatData +=
                                                " (YOU):" +  timeline.getJSONObject(i).getString("content")+"\n";
                                    } else{
                                        chatData +=
                                                 " (LECTURER):" +  timeline.getJSONObject(i).getString("content")+"\n";
                                    }
                                }

                                chatTV.setText(chatData);
                                
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Snackbar.make(chatTV,"Sorry couldn't fetch messages. Try again",Snackbar.LENGTH_LONG).show();
                        }
                    }){
                @Override
                public Map<String, String> getParams(){
                    Map<String, String> params = new HashMap<>();
                    params.put("sid", String.valueOf(sid));
                    params.put("cid",cid );
                    return params;
                }
            };
    
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
    
        }

    public void postComment(){
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, postCommentUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            messageTV.setText("");
                            getComments();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Snackbar.make(chatTV,"Sorry couldn't send your comment. Try again",Snackbar.LENGTH_LONG).show();

                        }
                    }){
                @Override
                public Map<String, String> getParams(){
                    Map<String, String> params = new HashMap<>();
                    params.put("cid",cid );
                    params.put("sid", String.valueOf(sid));
                    params.put("comment", messageTV.getText().toString());
                    params.put("from", "Student");
                    return params;
                }
            };

            // Add the request to the RequestQueue.
            queue.add(stringRequest);

        };


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fab){
            finish();
        }

        else if(view.getId() == R.id.chatTextView){
            getComments();
        }

        else if(view.getId() == R.id.sendMessageButton){

            postComment();
            getComments();
        }
    }
}
