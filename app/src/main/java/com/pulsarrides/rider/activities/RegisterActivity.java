package com.pulsarrides.rider.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pulsarrides.rider.R;
import com.pulsarrides.rider.databinding.ActivityRegisterBinding;
import com.pulsarrides.rider.models.AuthResponse;
import com.pulsarrides.rider.models.User;
import com.pulsarrides.rider.network.ApiClient;
import com.pulsarrides.rider.utils.PrefsManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private PrefsManager prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = new PrefsManager(this);

        binding.btnRegister.setOnClickListener(v -> attemptRegister());

        binding.tvLogin.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        String name = binding.etName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, getString(R.string.error_passwords_mismatch), Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("phone", phone);
        body.put("password", password);
        body.put("role", "rider");

        ApiClient.getInstance(this).getService().register(body).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse auth = response.body();
                    saveAuth(auth);
                    navigateToHome();
                } else {
                    Toast.makeText(RegisterActivity.this,
                            getString(R.string.error_register_failed),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(RegisterActivity.this,
                        getString(R.string.error_network),
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
        binding.btnRegister.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
