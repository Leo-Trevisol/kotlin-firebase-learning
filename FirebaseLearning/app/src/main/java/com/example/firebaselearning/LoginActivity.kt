package com.example.firebaselearning

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebaselearning.databinding.ActivityLoginBinding
import com.example.firebaselearning.databinding.ActivityRegisterMoodBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val senha = binding.edtSenha.text.toString().trim()

            if (email.isNotEmpty() && senha.isNotEmpty()) {
                loginUsuario(email, senha)
            } else {
                Toast.makeText(this, "Preencha os campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRegistrar.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val senha = binding.edtSenha.text.toString().trim()

            if (email.isNotEmpty() && senha.length >= 6) {
                registrarUsuario(email, senha)
            } else {
                Toast.makeText(this, "Senha deve ter no mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun registrarUsuario(email: String, senha: String) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Usuário registrado com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Erro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loginUsuario(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login realizado!", Toast.LENGTH_SHORT).show()
                    finish()
                    // startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(this, "Erro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}