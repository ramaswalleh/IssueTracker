package com.ramaswalleh.issuetracker;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    EditText editTextIssueID, editTextWaterpointID, editTextWaterpointName;
    Spinner spinnerCasePadlock, spinnerTankValve, spinnerBaseStandBolt;
    RatingBar ratingBar;
    Button buttonAddUpdate;
    ListView listViewIssues;
    ProgressBar progressBar;

    List<Issue> issueList;

    boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextIssueID = findViewById(R.id.editTextIssueID);
        editTextWaterpointID = findViewById(R.id.editTextWaterpointID);
        editTextWaterpointName = findViewById(R.id.editTextWaterpointName);
        spinnerCasePadlock = findViewById(R.id.spinnerCasePadlock);
        spinnerTankValve = findViewById(R.id.spinnerTankValve);
        spinnerBaseStandBolt = findViewById(R.id.spinnerBaseStandBolt);
        ratingBar = findViewById(R.id.ratingBar);
        buttonAddUpdate = findViewById(R.id.buttonAddUpdate);
        listViewIssues = findViewById(R.id.listViewIssues);
        progressBar = findViewById(R.id.progressBar);

        issueList = new ArrayList<>();

        buttonAddUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUpdating) {
                    //updateIssue();
                } else {
                    createIssue();
                }
            }
        });
        //readIssues();
    }

    private void createIssue() {
        String waterpointID = editTextWaterpointID.getText().toString().trim();
        String waterpointName = editTextWaterpointName.getText().toString().trim();
        String casePadLock = spinnerCasePadlock.getSelectedItem().toString();
        String tankValve = spinnerTankValve.getSelectedItem().toString();
        String baseStandBolt = spinnerBaseStandBolt.getSelectedItem().toString();
        int rating = (int) ratingBar.getRating();

        if (TextUtils.isEmpty(waterpointID)) {
            editTextWaterpointID.setError("Please enter a water point ID!");
            editTextWaterpointID.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(waterpointName)) {
            editTextWaterpointName.setError("Please enter a water point name!");
            editTextWaterpointName.requestFocus();
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("waterpoint_id", waterpointID);
        params.put("waterpoint_name", waterpointName);
        params.put("case_padlock", casePadLock);
        params.put("tank_valve", tankValve);
        params.put("base_stand_bolt", baseStandBolt);
        params.put("rating", String.valueOf(rating));

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_ISSUE, params, CODE_POST_REQUEST);
        request.execute();
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;
        int requestMethod;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestMethod) {
            this.url = url;
            this.params = params;
            this.requestMethod = requestMethod;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(GONE);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    //refreshIssueList(object.getJSONArray("issues"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if (requestMethod == CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);

            if (requestMethod == CODE_GET_REQUEST)
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }
}