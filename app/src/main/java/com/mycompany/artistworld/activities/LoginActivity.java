package com.mycompany.artistworld.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.mycompany.artistworld.R;
import com.mycompany.artistworld.objects.AuthUser;
import com.mycompany.artistworld.objects.UserCredentials;
import com.mycompany.artistworld.rest.IdeaApiInterface;
import com.mycompany.artistworld.rest.ServiceGenerator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.username)
    EditText mUsername;
    @BindView(R.id.password)
    EditText mPassword;

    IdeaApiInterface ideaService;
    boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        ideaService = ServiceGenerator.createService(IdeaApiInterface.class);

        checkUserCredentials();
        Toast.makeText(this, String.valueOf(isLoggedIn), Toast.LENGTH_SHORT).show();

        //FirebaseCrash.report(new Exception("My first Android non-fatal error"));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSharedPreferences(getString(R.string.preference_name), MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(this);
    }

    @OnClick(R.id.btn_login)
    public void login() {
        String username = mUsername.getText().toString();
        String psw = mPassword.getText().toString();

        AuthUser authUser = new AuthUser(username, psw);

        Call<UserCredentials> call = ideaService.login(authUser);
        call.enqueue(new Callback<UserCredentials>() {
            @Override
            public void onResponse(Call<UserCredentials> call, Response<UserCredentials> response) {
                //mLoadingIndicator.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    updateCredentialsInPreference(response.body());
                    //showIdeaDataView();
                    Toast.makeText(getBaseContext(), "success", Toast.LENGTH_LONG).show();
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(getBaseContext(), "Unauthenticated", Toast.LENGTH_SHORT).show();
                    } else if (response.code() >= 400) {
                        Toast.makeText(getBaseContext(), "Client Error " + response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserCredentials> call, Throwable t) {
                // Log error here since request failed
                // mLoadingIndicator.setVisibility(View.INVISIBLE);
                Log.e(TAG, t.toString());
                //showErrorMessage();
            }
        });
    }

    private void updateCredentialsInPreference(UserCredentials userCredentials){
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.preference_name), MODE_PRIVATE).edit();
        editor.putInt(getString(R.string.user_id_key), userCredentials.getId());
        editor.putString(getString(R.string.token_key), userCredentials.getmToken());
        editor.apply();
    }

    private void checkUserCredentials(){
        //Retrieving and validating if user is logged in
        SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_name), MODE_PRIVATE);
        String restoredToken = prefs.getString(getString(R.string.token_key), null);
        prefs.registerOnSharedPreferenceChangeListener(this);

        if (restoredToken != null) {
            isLoggedIn = true;
            Toast.makeText(this, "You are logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            isLoggedIn = false;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //check what preference change
        if (key.equals(getString(R.string.token_key))){
            checkUserCredentials();
        }
    }
}
