package com.mycompany.artistworld.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.mycompany.artistworld.R;
import com.mycompany.artistworld.objects.AuthUser;
import com.mycompany.artistworld.objects.AuthUserForVote;
import com.mycompany.artistworld.objects.UserCredentials;
import com.mycompany.artistworld.rest.IdeaApiInterface;
import com.mycompany.artistworld.rest.ServiceGenerator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String TAG = SignUpActivity.class.getSimpleName();

    @BindView(R.id.email)
    EditText mEmail;
    @BindView(R.id.username)
    EditText mUsername;
    @BindView(R.id.password)
    EditText mPassword;

    IdeaApiInterface ideaService;
    boolean isLoggedIn;
    AuthUser mAuthUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        checkUserCredentials();

        ideaService = ServiceGenerator.createService(IdeaApiInterface.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSharedPreferences(getString(R.string.preference_name), MODE_PRIVATE).unregisterOnSharedPreferenceChangeListener(this);
    }

    @OnClick(R.id.sign_in_button)
    public void goToLogin(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.sign_up_button)
    public void createForVote(){
        String username = mUsername.getText().toString();
        String psw = mPassword.getText().toString();
        String email = mEmail.getText().toString();

        mAuthUser = new AuthUser(username, psw);

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(psw) || TextUtils.isEmpty(email) ){
            Toast.makeText(this, getString(R.string.all_fields_required), Toast.LENGTH_SHORT).show();
            return;
        }

        AuthUserForVote authUserForVote = new AuthUserForVote(email, psw, username);

        Call<ResponseBody> call = ideaService.createForVote(authUserForVote);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //mLoadingIndicator.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    loginUser(mAuthUser);
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(getBaseContext(), getString(R.string.unauthenticated), Toast.LENGTH_SHORT).show();
                    } else if (response.code() >= 400) {
                        Toast.makeText(getBaseContext(), getString(R.string.client_error) + response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                // mLoadingIndicator.setVisibility(View.INVISIBLE);
                Log.e(TAG, t.toString());
                //showErrorMessage();
            }
        });


    }

    public void loginUser(AuthUser authUser) {

        Call<UserCredentials> call = ideaService.login(authUser);
        call.enqueue(new Callback<UserCredentials>() {
            @Override
            public void onResponse(Call<UserCredentials> call, Response<UserCredentials> response) {
                //mLoadingIndicator.setVisibility(View.INVISIBLE);
                if (response.isSuccessful()) {
                    updateCredentialsInPreference(response.body());
                    //showIdeaDataView();
                    Toast.makeText(getBaseContext(), getString(R.string.logged_in), Toast.LENGTH_LONG).show();
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(getBaseContext(), getString(R.string.unauthenticated), Toast.LENGTH_SHORT).show();
                    } else if (response.code() >= 400) {
                        Toast.makeText(getBaseContext(), getString(R.string.client_error) + response.code() + " " + response.message(), Toast.LENGTH_LONG).show();
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
        isLoggedIn = restoredToken != null;

        if (isLoggedIn) {
            Toast.makeText(this, getString(R.string.logged_in), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
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
