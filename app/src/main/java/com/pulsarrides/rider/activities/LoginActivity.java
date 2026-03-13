package com.pulsarrides.rider.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pulsarrides.rider.databinding.ActivityLoginBinding;
import com.pulsarrides.rider.models.AuthResponse;
import com.pulsarrides.rider.models.User;
import com.pulsarrides.rider.network.ApiClient;
import com.pulsarrides.rider.utils.PrefsManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private PrefsManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = new PrefsManager(this);

        binding.btnLogin.setOnClickListener(v -> attemptLogin());

        binding.tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void attemptLogin() {
        String phone = binding.etPhone.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(com.pulsarrides.rider.R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        Map<String, String> body = new HashMap<>();
        body.put("phone", phone);
        body.put("password", password);

        ApiClient.getInstance(this).getService().login(body).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    saveAuth(auth);
                    navigateToHome();
                } else {
                    Toast.makeText(LoginActivity.this,
                            getString(com.pulsarrides.rider.R.string.error_login_failed),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(LoginActivity.this,
                        getString(com.pulsarrides.rider.R.string.error_network),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAuth(AuthResponse auth) {
        prefs.setToken(auth.getToken());
        User user = auth.getUser();
        if (user != null) {
            prefs.saveUser(user.getId(), user.getName(), user.getPhone(), user.getRole());
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        binding.btnLogin.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
