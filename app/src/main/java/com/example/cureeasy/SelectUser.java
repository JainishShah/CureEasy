package com.example.cureeasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class SelectUser extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener{
Button button;
String uuid,s1;
RadioButton user,doctor;
Spinner spinner;
TextView doctype;
    Map<String,String> result;
    FirebaseFunctions mFunctions;
    String dtype;
    String doctors[]={"Endocrinologist","Neurologist","Psychiatrist","Ophthalmologist","Urologist"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);
        button=findViewById(R.id.button);
        button.setOnClickListener(this);
        spinner=findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        user=findViewById(R.id.radio_user);
        doctor=findViewById(R.id.radio_doctor);
        doctype=findViewById(R.id.text_doctor);
        mFunctions = FirebaseFunctions.getInstance();
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,doctors);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(aa);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("prev", MODE_PRIVATE);
        uuid=pref.getString("userid",null);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }

                        // Get new Instance ID token
                        s1 = task.getResult().getToken();
                        callToken();
                        // Log and toast

                    }
                });

    }

    @Override
    public void onClick(View v) {
        if(user.isChecked())
        {
Intent i=new Intent(getApplicationContext(),ChatActivity.class);
startActivity(i);
        }
        else
        {
         if(dtype!=null)
         {
            addtodoctor();
        }}

    }

    private Task<Map<String,String>> addToken(String text) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("token", text);
        data.put("userid", uuid);

        return mFunctions
                .getHttpsCallable("addtoken")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Map<String,String>>() {
                    @Override
                    public Map<String,String> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        result = (Map<String,String>) task.getResult().getData();
                        return result;
                    }
                });
    }

    public void callToken()
    {
        addToken(s1)
                .addOnCompleteListener(new OnCompleteListener<Map<String,String>>() {
                    @Override
                    public void onComplete(@NonNull Task<Map<String,String>> task) {
                        if (!task.isSuccessful()) {

                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }

                            // ...
                        }
                        else
                        {

                        }
                        // ...
                    }
                });

    }
public void addtodoctor()
{
    addDoctor()
            .addOnCompleteListener(new OnCompleteListener<Map<String,String>>() {
                @Override
                public void onComplete(@NonNull Task<Map<String,String>> task) {
                    if (!task.isSuccessful()) {

                        Exception e = task.getException();
                        if (e instanceof FirebaseFunctionsException) {
                            FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                            FirebaseFunctionsException.Code code = ffe.getCode();
                            Object details = ffe.getDetails();
                        }

                        // ...
                    }
                    else
                    { Intent i=new Intent(getApplicationContext(),ChatActivity.class);
                        startActivity(i);

                    }
                    // ...
                }
            });

}
    private Task<Map<String,String>> addDoctor() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("userid", uuid);
        data.put("type",dtype);

        return mFunctions
                .getHttpsCallable("adddoctor")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Map<String,String>>() {
                    @Override
                    public Map<String,String> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        result = (Map<String,String>) task.getResult().getData();
                        return result;
                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        dtype=doctors[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
