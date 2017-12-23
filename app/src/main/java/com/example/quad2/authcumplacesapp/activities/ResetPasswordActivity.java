package com.example.quad2.authcumplacesapp.activities;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

  @BindView(R.id.email)
  EditText emailInput;
  private ProgressDialog dialog;
  private FirebaseAuth auth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_reset_password);
    ButterKnife.bind(this);
    auth = FirebaseAuth.getInstance();
    dialog = new ProgressDialog(this);
    dialog.setCancelable(false);
    dialog.setMessage("Please Wait...");
  }

  @OnClick({R.id.btn_reset_password, R.id.btn_back})
  public void onViewClicked(View view) {
    switch (view.getId()) {
      case R.id.btn_reset_password:
        resetBtn();
        break;
      case R.id.btn_back:
        finish();
        break;
    }
  }

  private void resetBtn() {
    String email = emailInput.getText().toString();
    if (TextUtils.isEmpty(email)) {
      Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
      return;
    }
    dialog.show();
    auth.sendPasswordResetEmail(email)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            dialog.dismiss();
            if (task.isSuccessful()) {
              Toast.makeText(ResetPasswordActivity.this,
                  "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT)
                  .show();
            } else {
              Toast.makeText(ResetPasswordActivity.this, "Failed to send reset email!",
                  Toast.LENGTH_SHORT).show();
            }
          }
        });
  }
}
