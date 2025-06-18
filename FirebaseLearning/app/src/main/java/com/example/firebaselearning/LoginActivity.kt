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

        // Link "Esqueceu a senha?"
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

    /**
     * Registra um novo usuário com e-mail e senha no Firebase.
     * Após o registro, envia automaticamente um e-mail de verificação.
     * Isso é importante para garantir que o e-mail usado é válido.
     */
    private fun registrarUsuario(email: String, senha: String) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // Envia o e-mail de verificação
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                // Mostra mensagem solicitando verificação de e-mail
                                Toast.makeText(this, "Usuário registrado! Verifique seu e-mail para ativar a conta.", Toast.LENGTH_LONG).show()
                                finish() // Fecha a tela de registro
                            } else {
                                Toast.makeText(this, "Erro ao enviar e-mail de verificação: ${verifyTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Erro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    /**
     * Realiza login com e-mail e senha.
     * Verifica se o e-mail foi confirmado antes de permitir acesso ao app.
     * Caso não esteja verificado, o login é bloqueado.
     */
    private fun loginUsuario(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        //  E-mail foi verificado, pode entrar
                        Toast.makeText(this, "Login realizado!", Toast.LENGTH_SHORT).show()
                        finish()
                        // Aqui você pode redirecionar para a MainActivity se quiser
                    } else {
                        //  E-mail não verificado: bloqueia acesso e desloga
                        Toast.makeText(this, "Por favor, verifique seu e-mail antes de fazer login.", Toast.LENGTH_LONG).show()
                        auth.signOut()
                    }
                } else {
                    Toast.makeText(this, "Erro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
