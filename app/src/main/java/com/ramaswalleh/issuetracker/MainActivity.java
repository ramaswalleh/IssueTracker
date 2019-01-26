package com.ramaswalleh.issuetracker;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
                    updateIssue();
                } else {
                    createIssue();
                }
            }
        });
        readIssues();
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

    private void readIssues() {
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_ISSUE, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void updateIssue() {
        String issueID = editTextIssueID.getText().toString();
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
        params.put("id", issueID);
        params.put("waterpoint_id", waterpointID);
        params.put("waterpoint_name", waterpointName);
        params.put("case_padlock", casePadLock);
        params.put("tank_valve", tankValve);
        params.put("base_stand_bolt", baseStandBolt);
        params.put("rating", String.valueOf(rating));

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_UPDATE_ISSUE, params, CODE_POST_REQUEST);
        request.execute();

        buttonAddUpdate.setText("Add");

        editTextWaterpointID.setText("");
        editTextWaterpointName.setText("");
        spinnerCasePadlock.setSelection(0);
        spinnerTankValve.setSelection(0);
        spinnerBaseStandBolt.setSelection(0);
        ratingBar.setRating(0);

        isUpdating = false;
    }

    private void deleteIssue(int id) {
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_DELETE_ISSUE + id, null, CODE_GET_REQUEST);
        request.execute();
    }

    private void refreshIssueList(JSONArray issues) throws JSONException {
        issueList.clear();

        for (int i = 0; i < issues.length(); i++) {
            JSONObject obj = issues.getJSONObject(i);

            issueList.add(new Issue(
                    obj.getInt("id"),
                    obj.getInt("waterpoint_id"),
                    obj.getString("waterpoint_name"),
                    obj.getString("case_padlock"),
                    obj.getString("tank_valve"),
                    obj.getString("base_stand_bolt"),
                    obj.getInt("rating")
            ));
        }

        IssueAdapter adapter = new IssueAdapter(issueList);
        listViewIssues.setAdapter(adapter);
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
                    refreshIssueList(object.getJSONArray("issues"));
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

    class IssueAdapter extends ArrayAdapter<Issue> {
        List<Issue> issueList;

        public IssueAdapter(List<Issue> issueList) {
            super(MainActivity.this, R.layout.layout_issue_list, issueList);
            this.issueList = issueList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View listViewItem = inflater.inflate(R.layout.layout_issue_list, null, true);

            TextView textViewWaterPointName = listViewItem.findViewById(R.id.textViewWaterPointName);
            TextView textViewUpdate = listViewItem.findViewById(R.id.textViewUpdate);
            TextView textViewDelete = listViewItem.findViewById(R.id.textViewDelete);

            final Issue issue = issueList.get(position);

            textViewWaterPointName.setText(issue.getWaterPointName());

            textViewUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isUpdating = true;

                    editTextIssueID.setText(String.valueOf(issue.getId()));
                    editTextWaterpointID.setText(String.valueOf(issue.getWaterPointId()));
                    editTextWaterpointName.setText(issue.getWaterPointName());
                    spinnerCasePadlock.setSelection(((ArrayAdapter<String>) spinnerCasePadlock.getAdapter()).getPosition(issue.getCasePadlock()));
                    spinnerTankValve.setSelection(((ArrayAdapter<String>) spinnerTankValve.getAdapter()).getPosition(issue.getTankValve()));
                    spinnerBaseStandBolt.setSelection(((ArrayAdapter<String>) spinnerBaseStandBolt.getAdapter()).getPosition(issue.getBaseStandBolt()));
                    ratingBar.setRating(issue.getRating());

                    buttonAddUpdate.setText("Update");
                }
            });

            textViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("Delete " + issue.getWaterPointName())
                            .setMessage("Are you sure you want to clear this issue entry?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteIssue(issue.getId());
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            });
            return listViewItem;
        }
    }
}