package com.example.shyamjeelakara.integration;

import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shyam.jeelakara on 16/03/2018.
 */

public class CustomFacebookCallback implements FacebookCallback<LoginResult> {
    String TAG = "CustomFacebookCallback";

    FacebookUser user;
    FacebookLoginListener listener;

    public CustomFacebookCallback(FacebookLoginListener listener) {
        this.listener = listener;
        user = new FacebookUser();

    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        // App code
        Log.i(TAG, "Login successful...!");

        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    user.setId(object.getString("id"));
                    user.setFirstName(object.getString("first_name"));
                    user.setLastName(object.getString("last_name"));
                    user.setUserEmail(object.getString("email"));

                    String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                    user.setImageURL(profilePicUrl);

                    listener.onSuccess(user);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "email, id, name, first_name, last_name, gender, picture"); //,*/" birthday, profile_pic");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onCancel() {
        // App code
        Log.i(TAG, "Cancel button tapped...!");
    }

    @Override
    public void onError(FacebookException exception) {
        // App code
        Log.i(TAG, "Login error...!");
    }
}
