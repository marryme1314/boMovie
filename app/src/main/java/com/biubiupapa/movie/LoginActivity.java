package com.biubiupapa.movie;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText etPhone;
    private EditText etCode;
    private TextView tvGetCode;
    private Button btnLogin;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etPhone = findViewById(R.id.etPhone);
        etCode = findViewById(R.id.etCode);
        tvGetCode = findViewById(R.id.tvGetCode);
        btnLogin = findViewById(R.id.btnLogin);

        tvGetCode.setOnClickListener(v -> sendCode());
        btnLogin.setOnClickListener(v -> doLogin());

        findViewById(R.id.llWechat).setOnClickListener(v -> loginWithWechat());
        findViewById(R.id.llQQ).setOnClickListener(v -> loginWithQQ());

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateLoginButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void updateLoginButton() {
        boolean canLogin = etPhone.getText().length() == 11 && etCode.getText().length() >= 4;
        btnLogin.setEnabled(canLogin);
        btnLogin.setAlpha(canLogin ? 1.0f : 0.5f);
    }

    private void sendCode() {
        String phone = etPhone.getText().toString().trim();
        if (phone.length() != 11) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        startCountDown();
        Toast.makeText(this, "验证码已发送至 " + phone.substring(0, 3) + "****" + phone.substring(7), Toast.LENGTH_SHORT).show();
    }

    private void startCountDown() {
        tvGetCode.setEnabled(false);
        tvGetCode.setTextColor(getResources().getColor(R.color.text_hint));

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvGetCode.setText(millisUntilFinished / 1000 + "秒后重发");
            }

            @Override
            public void onFinish() {
                tvGetCode.setEnabled(true);
                tvGetCode.setTextColor(getResources().getColor(R.color.primary));
                tvGetCode.setText("获取验证码");
            }
        }.start();
    }

    private void doLogin() {
        String phone = etPhone.getText().toString().trim();
        String code = etCode.getText().toString().trim();

        if (phone.length() != 11) {
            Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
            return;
        }

        if (code.length() < 4) {
            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }

        android.content.SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        sp.edit()
                .putString("phone", phone)
                .putString("name", "用户" + phone.substring(7))
                .putBoolean("logged_in", true)
                .apply();

        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void loginWithWechat() {
        Toast.makeText(this, "微信登录中...", Toast.LENGTH_SHORT).show();

        new android.os.Handler().postDelayed(() -> {
            android.content.SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
            sp.edit()
                    .putString("phone", "13800138000")
                    .putString("name", "微信用户")
                    .putBoolean("logged_in", true)
                    .apply();

            Toast.makeText(this, "微信登录成功", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }, 1500);
    }

    private void loginWithQQ() {
        Toast.makeText(this, "QQ登录中...", Toast.LENGTH_SHORT).show();

        new android.os.Handler().postDelayed(() -> {
            android.content.SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
            sp.edit()
                    .putString("phone", "13900139000")
                    .putString("name", "QQ用户")
                    .putBoolean("logged_in", true)
                    .apply();

            Toast.makeText(this, "QQ登录成功", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }, 1500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}