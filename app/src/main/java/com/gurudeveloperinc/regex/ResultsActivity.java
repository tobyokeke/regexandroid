package com.gurudeveloperinc.regex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.gurudeveloperinc.regex.Constants.queue;
import static com.gurudeveloperinc.regex.Constants.transcriptUrl;

public class ResultsActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    TextView nameTV,idTV,progTV,cwaTV;
    String name, studentid, prog,cwa;
    SharedPreferences userPrefs;
    SharedPreferences.Editor prefEditor;
    ImageButton logoutButton;
    int totalAs = 0, totalBs = 0,totalCs = 0,totalDs = 0,totalFs = 0;
    public int pageCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);



        // get shared preferences
        userPrefs = this.getSharedPreferences("com.gurudeveloperinc.regex", Context.MODE_PRIVATE);


        nameTV = (TextView) findViewById(R.id.nameTextView);
        idTV = (TextView) findViewById(R.id.idNumberTextView);
        progTV = (TextView) findViewById(R.id.progTextView);
        cwaTV = (TextView) findViewById(R.id.cwaTextView);
        pageCount = 1;

        name = userPrefs.getString("surname","") + " " + userPrefs.getString("othernames","");
        studentid = userPrefs.getString("studentid","");
        prog = userPrefs.getString("programme","");
        cwa = userPrefs.getString("cwa","0");

        //instantiate the request queue
        queue = Volley.newRequestQueue(this);

        getTranscript(studentid);


        nameTV.setText(name);
        idTV.setText(studentid);
        progTV.setText(prog);

        logoutButton = (ImageButton) findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get shared pref editor
                prefEditor = userPrefs.edit();
                prefEditor.putInt("isLoggedIn", 0);
                prefEditor.commit();

                Toast.makeText(getApplicationContext(),"You have been logged out", Toast.LENGTH_LONG).show();
                Intent nextActivity = new Intent(ResultsActivity.this, LoginActivity.class);
                startActivity(nextActivity);
                finish();



            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ResultsActivity.this,FeedbackActivity.class));
            }
        });

    }

    public String getLetter(String gradeString){
        int grade = Integer.valueOf(gradeString);

        if(grade < 40) {
            totalFs++;
            return "F";
        } else if (grade >= 40 && grade < 50){
            totalDs++;
            return "D";
        } else if(grade >= 50 && grade <  60){
            totalCs++;
            return "C";
        } else if(grade >= 60 && grade < 70){
            totalBs++;
            return "B";
        } else if(grade >=70 && grade <= 100){
            totalAs++;
            return "A";
        } else {
            return "F";
        }


    }

    public void getTranscript(final String stud){
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, transcriptUrl ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String[] semKeys = {"1001","1002","2001","2002","3001","3002","4001","4002"};


                        JSONObject jsonResponse = null;

                        try {
                            jsonResponse = new JSONObject(response);

                            cwa = jsonResponse.getString("cwa");

                            prefEditor = userPrefs.edit();
                            prefEditor.putString("cwa", cwa);
                            prefEditor.commit();

                            for(int i = 0; i < semKeys.length; i++){
                                JSONArray thisArray = jsonResponse.getJSONArray(semKeys[i]);
                                if(thisArray.length() > 0 ) {
                                    pageCount++;

                                }

                                for(int j =0; j < thisArray.length(); j++) {
                                    getLetter(thisArray.getJSONObject(j).getString("totalgrade"));

                                }

                            }

                            prefEditor = userPrefs.edit();
                            prefEditor.putInt("totalAs", totalAs);
                            prefEditor.putInt("totalBs", totalBs);
                            prefEditor.putInt("totalCs", totalCs);
                            prefEditor.putInt("totalDs", totalDs);
                            prefEditor.putInt("totalFs", totalFs);
                            prefEditor.commit();


                            cwaTV.setText("CWA - " + cwa + "%");

                            Log.e("regex", String.valueOf(totalAs));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Snackbar.make(nameTV,"Sorry and error occurred.",Snackbar.LENGTH_LONG).show();

                    }
                }){
            @Override
            public Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("studentid", stud  );
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }



        return super.onOptionsItemSelected(item);
    }



    public static class PlaceholderFragment extends Fragment implements View.OnClickListener, ListView.OnItemClickListener {

        String transcript;
        TextView textView;
        TextView profileTV;
        ListView resultsLV;
        Button changePasswordButton, changeProfilePicButton;
        int semester;
        ArrayList<String> transcriptList = new ArrayList<>();
        ArrayList<String> cidList = new ArrayList<>();


        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {

            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public String getLetter(String gradeString){
            int grade = Integer.valueOf(gradeString);

            if(grade < 40) {
                return "F";
            } else if (grade >= 40 && grade < 50){
                return "D";
            } else if(grade >= 50 && grade <  60){
                return "C";
            } else if(grade >= 60 && grade < 70){
                return "B";
            } else if(grade >=70 && grade <= 100){
                return "A";
            } else {
                return "F";
            }


        }


        public void getTranscript(final String stud,final int semester){
            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, transcriptUrl ,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            int semesterNo = semester - 2;
                            int items = 0;  // number of courses that results have been released for in a semester
                            int sumTotalGrade = 0;  // sum of total available grades
                            String[] semKeys = {"1001","1002","2001","2002","3001","3002","4001","4002"};


                            JSONObject jsonResponse = null;

                            try {
                                jsonResponse = new JSONObject(response);



                                    JSONArray sem1001 = jsonResponse.getJSONArray(semKeys[semesterNo]);

                                    for (int i = 0; i < sem1001.length(); i++) {
                                        cidList.add(sem1001.getJSONObject(i).getString("cid"));
                                        transcriptList.add(sem1001.getJSONObject(i).getString("name") + "        | " +
                                                sem1001.getJSONObject(i).getString("totalgrade") + "% | "
                                                + getLetter(sem1001.getJSONObject(i).getString("totalgrade"))
                                        );
                                        sumTotalGrade += sem1001.getJSONObject(i).getInt("totalgrade");
                                        items++;
                                    }

                                if(items == 0) items = 1;
                                int swa = sumTotalGrade/items;

                                // display proper message if theres no result for a semester
                                if(sumTotalGrade > 0) {
                                    transcriptList.add("Your Semester Weighted Average: " + swa + "%");
                                } else {
                                    transcriptList.add("No results yet");
                                }


                                if(getActivity().getApplicationContext() != null) {
                                    ArrayAdapter<String> adapter =
                                            new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, transcriptList) {
                                                @Override
                                                public View getView(int position, View convertView, ViewGroup parent) {
                                                    View view = super.getView(position, convertView, parent);

                                                    TextView textView = (TextView) view.findViewById(android.R.id.text1);

                                                /*YOUR CHOICE OF COLOR*/
                                                    textView.setTextColor(Color.BLACK);

                                                    return view;
                                                }
                                            };
                                    resultsLV.setAdapter(adapter);
                                }


                            } catch(Exception e) {
                                e.printStackTrace();
                            }


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Snackbar.make(textView,"Sorry and error occurred.",Snackbar.LENGTH_LONG).show();

                        }
                    }){
                @Override
                public Map<String, String> getParams(){
                    Map<String, String> params = new HashMap<>();
                    params.put("studentid", stud  );
                    return params;
                }
            };

            // Add the request to the RequestQueue.
            queue.add(stringRequest);

        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_results, container, false);
            textView = (TextView) rootView.findViewById(R.id.section_label);
            resultsLV = (ListView) rootView. findViewById(R.id.resultsListView);
            resultsLV.setOnItemClickListener(this);
            profileTV = (TextView) rootView.findViewById(R.id.profileTextView);
            changePasswordButton = (Button) rootView.findViewById(R.id.changePasswordButton);
            changeProfilePicButton = (Button) rootView.findViewById(R.id.changeProfilePicButton);
            semester = getArguments().getInt(ARG_SECTION_NUMBER);
            String level = "";

            // get shared preferences
            SharedPreferences userPrefs = getActivity().getSharedPreferences("com.gurudeveloperinc.regex", Context.MODE_PRIVATE);

            String studentid = userPrefs.getString("studentid","");

            //instantiate the request queue
            queue = Volley.newRequestQueue(getActivity());

            getTranscript(studentid,semester);

            switch (semester){
                case 2:
                    level = "100 level First Semester";
                    break;

                case 3:
                    level = "100 level Second Semester";
                    break;

                case 4:
                    level = "200 level First Semester";
                    break;

                case 5:
                    level = "200 level Second Semester";
                    break;

                case 6:
                    level = "300 level First Semester";
                    break;

                case 7:
                    level = "300 level Second Semester";
                    break;

                case 8:
                    level = "400 level First Semester";
                    break;

                case 9:
                    level = "400 level Second Semester";
                    break;

                default:
                    level = "100 level First Semester";
            }


            textView.setText("Your results for " + level );

            if(semester == 1){

                String society = userPrefs.getString("society","");
                String email = userPrefs.getString("email","");
                String nationality = userPrefs.getString("nationality","");
                String session = userPrefs.getString("session","");
                String phone = userPrefs.getString("phone","");
                int totalAs = userPrefs.getInt("totalAs",0);
                int totalBs = userPrefs.getInt("totalBs",0);
                int totalCs = userPrefs.getInt("totalCs",0);
                int totalDs = userPrefs.getInt("totalDs",0);
                int totalFs = userPrefs.getInt("totalFs",0);

                String profile = "Society:      " + society + "\n";
                profile += "Email:         " + email + "\n";
                profile += "Nationality: " + nationality + "\n";
                profile += "Session:     " + session + "\n";
                profile += "Phone:        " + phone + "\n";

                profile += "\nSince you joined regent, you have gotten\n";

                profile +=  totalAs + " As | ";
                profile +=  totalBs + " Bs | ";
                profile +=  totalCs + " Cs | ";
                profile +=  totalDs + " Ds | ";
                profile +=  totalFs + " Fs  ";


                profileTV.setVisibility(View.VISIBLE);
                changeProfilePicButton.setVisibility(View.VISIBLE);
                changeProfilePicButton.setOnClickListener(this);

                changePasswordButton.setVisibility(View.VISIBLE);
                changePasswordButton.setOnClickListener(this);

                profileTV.setText(profile);
                textView.setText("Your profile");
            }

            return rootView;
        }


        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
           String cid = "";
            if(i < cidList.size()){
                cid = cidList.get(i);

                SharedPreferences userPrefs = getActivity().getSharedPreferences("com.gurudeveloperinc.regex", Context.MODE_PRIVATE);
                int sid = userPrefs.getInt("sid",0);

                Intent nextActivity = new Intent(getActivity(),CommentActivity.class);

                nextActivity.putExtra("sid", sid);
                nextActivity.putExtra("cid",cid);
                startActivity(nextActivity);

            }

        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.changePasswordButton){
                Intent nextActivity = new Intent(getActivity(),ChangePasswordActivity.class);
                startActivity(nextActivity);
            }

            if(view.getId() == R.id.changeProfilePicButton){

                Toast.makeText(getActivity().getApplicationContext(),"Downloading",Toast.LENGTH_LONG).show();
            }
        }

    }


    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {

            // Show 3 total pages.
            return 9;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 1";
                case 2:
                    return "SECTION 1";
            }
            return null;
        }
    }
}
