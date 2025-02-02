package com.irfan.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AiActivity extends AppCompatActivity {

    CheckBox checkboxBedroom, checkboxLivingRoom, checkboxKitchen, checkboxBathroom, checkboxModern,
            checkboxRustic, checkboxMinimalist, checkboxClassic;
    EditText inputLength, inputWidth;
    ScrollView scrollView;

    TextView suggestionsOutput;

    String apiKey = "" ;
    public static final okhttp3.MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(120, TimeUnit.SECONDS)
            .build();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        Button generateSuggestionsBtn = findViewById(R.id.generateSuggestionsBtn);
        inputLength = findViewById(R.id.input_length);
        inputWidth = findViewById(R.id.input_width);
         suggestionsOutput = findViewById(R.id.suggestionsOutput);
        scrollView = findViewById(R.id.scrollView);



        checkboxBedroom = findViewById(R.id.checkbox_bedroom);
        checkboxLivingRoom = findViewById(R.id.checkbox_livingroom);
        checkboxKitchen = findViewById(R.id.checkbox_kitchen);
        checkboxBathroom = findViewById(R.id.checkbox_bathroom);

        checkboxModern = findViewById(R.id.checkbox_modern);
        checkboxRustic = findViewById(R.id.checkbox_rustic);
        checkboxMinimalist = findViewById(R.id.checkbox_minimalist);
        checkboxClassic = findViewById(R.id.checkbox_classic);

        generateSuggestionsBtn.setOnClickListener(v -> {
            StringBuilder preferences = new StringBuilder("Your Design Preferences:\n\n");


            if (checkboxBedroom.isChecked()) preferences.append("- Bedroom\n");
            if (checkboxLivingRoom.isChecked()) preferences.append("- Living Room\n");
            if (checkboxKitchen.isChecked()) preferences.append("- Kitchen\n");
            if (checkboxBathroom.isChecked()) preferences.append("- Bathroom\n");

            if (checkboxModern.isChecked()) preferences.append("- Modern\n");
            if (checkboxRustic.isChecked()) preferences.append("- Rustic\n");
            if (checkboxMinimalist.isChecked()) preferences.append("- Minimalist\n");
            if (checkboxClassic.isChecked()) preferences.append("- Classic\n");

            String lengthText = inputLength.getText().toString().trim();
            String widthText = inputWidth.getText().toString().trim();

            if (!lengthText.isEmpty() && !widthText.isEmpty()) {
                preferences.append("\nRoom Dimensions:\n")
                        .append("- Length: ").append(lengthText).append(" ft\n")
                        .append("- Width: ").append(widthText).append(" ft\n");
            } else {
                Toast.makeText(this, "Please enter valid room dimensions!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (preferences.length() > 0) {
                suggestionsOutput.setVisibility(View.VISIBLE);

                callAPI(String.valueOf(preferences));

                scrollView.post(() -> scrollView.smoothScrollTo(0, suggestionsOutput.getTop()));
            } else {
                Toast.makeText(this, "Please select at least one preference!", Toast.LENGTH_SHORT).show();
            }
        });
    } //onCreate



    void callAPI(String designInfo ){

        String prompt = "Create interior design with the following details provide information suggestions and improvement according to the these requirements"  + designInfo;

      //  Toast.makeText(this, prompt, Toast.LENGTH_SHORT).show();

        JSONObject jsonBody = new JSONObject();
        try {

            jsonBody.put("model","gpt-3.5-turbo");
            JSONArray messageArr = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("role","user");
            obj.put("content",prompt);
            messageArr.put(obj);

            jsonBody.put("messages",messageArr);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(),JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer sk-ijklmnopqrstuvwxijklmnopqrstuvwxijklmnop")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                suggestionsOutput.setText("Failed to load response due to "+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    JSONObject  jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        ResponseBody responseBodyCopy = response.peekBody(Long.MAX_VALUE);
                        responseBodyCopy.string();
                        suggestionsOutput.setText((CharSequence) responseBodyCopy);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else{
                    suggestionsOutput.setText("Failed to load response");
                }
            }
        });





    }




}