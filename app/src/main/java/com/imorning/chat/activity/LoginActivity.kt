package com.imorning.chat.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.imorning.chat.databinding.ActivityLoginBinding
import com.imorning.common.action.Login
import com.imorning.common.constant.StatusCode.OK

private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginGoButton.setOnClickListener {
            val account = binding.loginAccountEdit.text.toString().trim()
            val password = binding.loginPasswordEdit.text.toString().trim()
            if (account.isEmpty() || password.isEmpty()) {
                Snackbar.make(binding.root, "账号密码不能为空", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val result = Login.run(
                account = account,
                password = password
            )
            if (result != OK) {
                Snackbar.make(binding.root, "登陆失败: $result", Snackbar.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                this.finish()
            }
        }
    }
}