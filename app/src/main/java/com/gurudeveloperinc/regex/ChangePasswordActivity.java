package com.gurudeveloperinc.regex;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.gurudeveloperinc.regex.Constants.changePasswordUrl;
import static com.gurudeveloperinc.regex.Constants.queue;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences userPrefs;
    String name, studentid, prog;
    EditText currentPasswordEditText,newPasswordEditText,confirmPasswordEditText;
    TextView nameTV,idTV,progTV;
    FloatingActionButton fab;
    Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // get shared preferences
        userPrefs = this.getSharedPreferences("com.gurudeveloperinc.regex", Context.MODE_PRIVATE);

        name = userPrefs.getString("surname","") + " " + userPrefs.getString("othernames","");
        studentid = userPrefs.getString("studentid","");
        prog = userPrefs.getString("programme","");

        currentPasswordEditText = (EditText) findViewById(R.id.currentPasswordEditText);
        newPasswordEditText = (EditText) findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        changePasswordButton = (Button) findViewById(R.id.changePasswordButton);

        changePasswordButton.setOnClickListener(this);

        nameTV = (TextView) findViewById(R.id.nameTextView);
        idTV = (TextView) findViewById(R.id.idNumberTextView);
        progTV = (TextView) findViewById(R.id.progTextView);

        name = userPrefs.getString("surname","") + " " + userPrefs.getString("othernames","");
        studentid = userPrefs.getString("studentid","");
        prog = userPrefs.getString("programme","");

        nameTV.setText(name);
        idTV.setText(studentid);
        progTV.setText(prog);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        //instantiate the request queue
        queue = Volley.newRequestQueue(this);


    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.fab){
            finish();
        }

        if(view.getId() == R.id.changePasswordButton){
            if(newPasswordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())){
                changePassword();
            } else{
                Snackbar.make(newPasswordEditText,"New passwords don't match",Snackbar.LENGTH_LONG).show();
            }
        }

    }

        public void changePassword(){
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, changePasswordUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                if(response.equals("1")){

                                    Toast.makeText(getApplicationContext(),"Passwords changed successfully",Toast.LENGTH_LONG).show();
                                    finish();
                                } else{
                                    Snackbar.make(newPasswordEditText,"Current password is wrong",Snackbar.LENGTH_LONG).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Snackbar.make(newPasswordEditText,"Sorry an error occurred",Snackbar.LENGTH_LONG).show();

                            }
                        }){
                    @Override
                    public Map<String, String> getParams(){
                        Map<String, String> params = new HashMap<>();
                        params.put("studentid", studentid);
                        params.put("oldpassword", currentPasswordEditText.getText().toString());
                        params.put("password", newPasswordEditText.getText().toString());
                        return params;
                    }
                };

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            } ;

}
