package com.example.quad2.authcumplacesapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

  @BindView(R.id.email)
  EditText emailInput;
  @BindView(R.id.password)
  EditText passwordInput;
  private ProgressDialog progressDialog;
  private FirebaseAuth auth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
    ButterKnife.bind(this);
    auth = FirebaseAuth.getInstance();
    progressDialog = new ProgressDialog(this);
    progressDialog.setMessage("Please Wait...");
    progressDialog.setCancelable(false);
  }

  @Override
  public void onStart() {
    super.onStart();
    // Check if user is signed in (non-null) and update UI accordingly.
    FirebaseUser currentUser = auth.getCurrentUser();
  }

  @OnClick({R.id.btn_register, R.id.btn_sign_in})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.btn_register:
        signUpBtn();
        break;
      case R.id.btn_sign_in:
        finish();
        break;
    }
  }

  private void signUpBtn() {
    String email = emailInput.getText().toString();
    String password = passwordInput.getText().toString();

    if (TextUtils.isEmpty(email)) {
      Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
      return;
    }

    if (TextUtils.isEmpty(password)) {
      Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
      return;
    }

    if (password.length() < 6) {
      Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!",
          Toast.LENGTH_SHORT).show();
      return;
    }

    progressDialog.show();
    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
        new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            progressDialog.dismiss();

            if (!task.isSuccessful()) {
              Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(),
                  Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(RegisterActivity.this, "User Created!", Toast.LENGTH_SHORT).show();
              startActivity(new Intent(RegisterActivity.this, MapActivity.class));
              finish();
            }

          }
        });

  }
}
