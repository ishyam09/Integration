package com.example.shyamjeelakara.integration;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements FacebookLoginListener, View.OnClickListener {

    private static final String EMAIL = "email";
    private static final String PROFILE = "public_profile";

    private CallbackManager callbackManager;

    private LoginButton loginButton;

    private Button fbButton, gpButton;

    private TextView userFirstName, userLastName, userEmail;

    private ImageView profileImage;

    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private SignInButton signInButton;


    private static final int RC_SIGN_IN = 9000;

    private static final String MAIN_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // FB INTEGRATION
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create(); // callBackManager Registration

        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL, PROFILE));

        userFirstName = findViewById(R.id.first_name);
        userLastName = findViewById(R.id.last_name);
        userEmail = findViewById(R.id.user_email);

        profileImage = findViewById(R.id.profile_picture);

        CustomFacebookCallback fb = new CustomFacebookCallback(this);
        loginButton.registerCallback(callbackManager, fb);


        //GOOGLE+
        signInButton = findViewById(R.id.sign_in_button);
        findViewById(R.id.sign_in_button).setOnClickListener(this);;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Log.i(MAIN_TAG, String.valueOf(mGoogleSignInClient));

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(MAIN_TAG, "onResume()!");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        Log.i(MAIN_TAG, "onActivityResult()!");
        Log.i("Print Result", String.valueOf(requestCode));

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    @Override
    public void onSuccess(FacebookUser user) {
        userFirstName.setText(user.getFirstName());
        userLastName.setText(user.getLastName());
        userEmail.setText(user.getUserEmail());

        Picasso.with(this).load(user.getImageURL()).into(profileImage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            // ...
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Log.i(MAIN_TAG, "Login Successful");
            Log.i(MAIN_TAG, String.valueOf(account.getEmail()));
            Log.i(MAIN_TAG, String.valueOf(account.getFamilyName()));
            Log.i("Given Name", String.valueOf(account.getGivenName()));
            Log.i(MAIN_TAG, String.valueOf(account.getDisplayName()));
            Log.i(MAIN_TAG, String.valueOf(account.getPhotoUrl()));

            userFirstName.setText(account.getFamilyName());
            userLastName.setText(account.getGivenName());
            Picasso.with(this).load(account.getPhotoUrl()).into(profileImage);

            // Signed in successfully, show authenticated UI.
//            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(MAIN_TAG, "signInResult:failed code=" + e.getStatusCode());
//            updateUI(null);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

//    protected void onClickListener() {
//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(EMAIL, PROFILE));
//    }
}
