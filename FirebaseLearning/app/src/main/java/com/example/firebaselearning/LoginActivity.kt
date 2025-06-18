package com.example.firebaselearning

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaselearning.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    // ViewBinding para acessar os elementos da UI
    private lateinit var binding: ActivityLoginBinding

    // Instância do FirebaseAuth para autenticação com e-mail e senha
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla o layout usando o ViewBinding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botão para realizar login
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val senha = binding.edtSenha.text.toString().trim()

            // Verifica se os campos estão preenchidos
            if (email.isNotEmpty() && senha.isNotEmpty()) {
                loginUsuario(email, senha)
            } else {
                Toast.makeText(this, "Preencha os campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Botão para registrar novo usuário
        binding.btnRegistrar.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val senha = binding.edtSenha.text.toString().trim()

            // Verifica se os campos estão preenchidos e a senha tem tamanho mínimo
            if (email.isNotEmpty() && senha.length >= 6) {
                registrarUsuario(email, senha)
            } else {
                Toast.makeText(this, "Senha deve ter no mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
            }
        }

        binding.txtEsqueceuSenha.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()

            if (email.isNotEmpty()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(this, "E-mail de redefinição enviado!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Digite o e-mail para redefinir a senha", Toast.LENGTH_SHORT).show()
            }
        }

        // Botão para voltar à tela anterior
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    // Função para registrar novo usuário com e-mail e senha no Firebase
    private fun registrarUsuario(email: String, senha: String) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Enviar e-mail de verificação
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                Toast.makeText(this, "Usuário registrado! Verifique seu e-mail para ativar a conta.", Toast.LENGTH_LONG).show()
                                finish() // Fecha a tela para que o usuário cheque o e-mail antes de entrar
                            } else {
                                Toast.makeText(this, "Erro ao enviar e-mail de verificação: ${verifyTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Erro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // Função para fazer login com e-mail e senha
    private fun loginUsuario(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        Toast.makeText(this, "Login realizado!", Toast.LENGTH_SHORT).show()
                        finish() // Fecha a tela e retorna
                        // Pode abrir a MainActivity aqui, se quiser
                    } else {
                        Toast.makeText(this, "Por favor, verifique seu e-mail antes de fazer login.", Toast.LENGTH_LONG).show()
                        auth.signOut() // Desloga para evitar acesso sem verificação
                    }
                } else {
                    Toast.makeText(this, "Erro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
