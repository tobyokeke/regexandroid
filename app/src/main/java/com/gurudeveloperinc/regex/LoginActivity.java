package com.gurudeveloperinc.regex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.gurudeveloperinc.regex.Constants.transcriptUrl;
import static com.gurudeveloperinc.regex.Constants.url;

public class LoginActivity extends AppCompatActivity  implements View.OnClickListener {

    Button buttonLogin;
    RequestQueue queue;
    SharedPreferences userPrefs;
    SharedPreferences.Editor prefEditor;
    EditText studentID,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        studentID = (EditText) findViewById(R.id.editTextStudentId);
        password = (EditText) findViewById(R.id.editTextPassword);

        // get shared preferences
        userPrefs = this.getSharedPreferences("com.gurudeveloperinc.regex", Context.MODE_PRIVATE);

        // move user to next activity if already logged in

        if (userPrefs.getInt("isLoggedIn", 0) == 1) {
            Intent nextActivity = new Intent(this, ResultsActivity.class);
            startActivity(nextActivity);
            finish();
        }


        buttonLogin = (Button)findViewById(R.id.buttonLogin);

        buttonLogin.setOnClickListener(this);

        //instantiate the request queue
        queue = Volley.newRequestQueue(this);
    }

    @Override
    public void onClick(View v) {
        final View view = v;
        switch (v.getId()) {



            case R.id.buttonLogin: {

                // make login request

                // Request a string response from the provided URL.
                final StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                JSONObject jsonResponse = null;
                                try {

                                    Log.e("regex",response);

                                    if (!response.isEmpty()) {
                                        //parse the json response
                                        jsonResponse = new JSONObject(response);
                                        // get shared pref editor
                                        prefEditor = userPrefs.edit();
                                        prefEditor.putInt("isLoggedIn", 1);
                                        prefEditor.putInt("sid",jsonResponse.getInt("sid"));
                                        prefEditor.putString("studentid",jsonResponse.getString("studentid"));
                                        prefEditor.putString("surname",jsonResponse.getString("surname"));
                                        prefEditor.putString("othernames",jsonResponse.getString("othernames"));
                                        prefEditor.putString("society",jsonResponse.getString("society"));
                                        prefEditor.putString("level",jsonResponse.getString("level"));
                                        prefEditor.putString("nationality",jsonResponse.getString("nationality"));
                                        prefEditor.putString("session",jsonResponse.getString("session"));
                                        prefEditor.putString("phone",jsonResponse.getString("phone"));
                                        prefEditor.putString("programme",jsonResponse.getJSONObject("programme").getString("progname"));
                                        prefEditor.putString("email",jsonResponse.getString("email"));
                                        prefEditor.commit();

                                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();


                                        startActivity(new Intent(LoginActivity.this,ResultsActivity.class));


                                    } else {
                                        Snackbar.make(view, "Wrong credentials. Please try again", Snackbar.LENGTH_LONG).show();

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Snackbar.make(view,"An error occurred. Please try again",Snackbar.LENGTH_LONG).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            Log.e("regex",error.getMessage());
                                Snackbar.make(view,"An error occurred. Please try again",Snackbar.LENGTH_LONG).show();

                            }
                        }){
                    @Override
                    public Map<String, String> getParams(){
                        Map<String, String> params = new HashMap<>();
                        params.put("studentid", studentID.getText().toString());
                        params.put("password", password.getText().toString());
                        return params;
                    }
                };


                // Add the request to the RequestQueue.
                queue.add(stringRequest);

                break;

            }

        }

    }





}
