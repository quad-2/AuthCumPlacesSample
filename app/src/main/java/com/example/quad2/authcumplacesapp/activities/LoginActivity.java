package com.example.quad2.authcumplacesapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.quad2.authcumplacesapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

  @BindView(R.id.email)
  EditText emailInput;
  @BindView(R.id.password)
  EditText passwordInput;
  private FirebaseAuth auth;
  private ProgressDialog dialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    ButterKnife.bind(this);
    dialog = new ProgressDialog(this);
    dialog.setMessage("Please Wait...");
    dialog.setCancelable(false);
    auth = FirebaseAuth.getInstance();
  }

  @OnClick({R.id.btn_login, R.id.btn_reset_password, R.id.btn_signup})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.btn_login:
        loginBtn();
        break;
      case R.id.btn_reset_password:
        startActivity(new Intent(this, ResetPasswordActivity.class));
        break;
      case R.id.btn_signup:
        startActivity(new Intent(this, RegisterActivity.class));
        break;
    }
  }

  private void loginBtn (){
    String email = emailInput.getText().toString();
    final String password = passwordInput.getText().toString();

    if (TextUtils.isEmpty(email)) {
      Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
      return;
    }

    if (TextUtils.isEmpty(password)) {
      Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
      return;
    }
    dialog.show();
    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this,
        new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {

            dialog.dismiss();
            if (!task.isSuccessful()) {
              // there was an error
              if (password.length() < 6) {
                passwordInput.setError(getString(R.string.minimum_password));
              } else {
                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
              }
            } else {
              startActivity(new Intent(LoginActivity.this, MapActivity.class));
              finish();
            }
          }
        });

  }
}
