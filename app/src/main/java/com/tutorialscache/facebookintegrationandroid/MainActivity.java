package com.tutorialscache.facebookintegrationandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    ProgressBar mPb;
    ImageView fbIv,userIv;
    TextView emailTv,nameTv;
    Context context;
    CallbackManager callbackManager;
    AccessToken access_token;
    GraphRequest request;
    private String email,facebook_uid,first_name,last_name,social_id,name,picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        getViews();
    }

    private void getViews() {
        mPb = findViewById(R.id.mPb);
        fbIv = findViewById(R.id.fbIv);
        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        userIv = findViewById(R.id.userIv);
        fbIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLoginSignup();
                mPb.setVisibility(View.VISIBLE);
            }
        });

    }

    private void facebookLoginSignup() {
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }
        LoginManager.getInstance().logInWithReadPermissions((Activity) context, Arrays.asList("email", "public_profile"));
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("response Success", "Login");
                        access_token = loginResult.getAccessToken();
                        Log.d("response access_token", access_token.toString());

                        request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                JSONObject json = response.getJSONObject();
                                try {
                                    if (json != null) {
                                        Log.d("response", json.toString());
                                        try {
                                            email = json.getString("email");
                                            emailTv.setText(email+"");
                                        } catch (Exception e) {
                                            Toast.makeText(context, "Sorry!!! Your email is not verified on facebook.", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        facebook_uid = json.getString("id");
                                        social_id = json.getString("id");
                                        first_name = json.getString("first_name");
                                        last_name = json.getString("last_name");
                                        name = json.getString("name");
                                        nameTv.setText(name+"");

                                        picture = "https://graph.facebook.com/" + facebook_uid + "/picture?type=large";
                                        Log.d("response",  " picture"+picture);
                                        Picasso.with(context).load(picture).placeholder(R.mipmap.ic_launcher).into(userIv);

                                        mPb.setVisibility(View.GONE);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.d("response problem", "problem" + e.getMessage());
                                }
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,first_name,last_name,link,email,picture");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(context, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    //region onResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
