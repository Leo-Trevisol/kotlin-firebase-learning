package com.example.firebaselearning

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaselearning.databinding.ActivityLoginGoogleBinding
import com.google.android.gms.auth.api.signin.* // Google Sign-In
import com.google.android.gms.common.api.ApiException // Para tratar erros de login
import com.google.firebase.auth.* // Firebase Authentication

class LoginGoogleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginGoogleBinding // ViewBinding da tela
    private lateinit var googleSignInClient: GoogleSignInClient // Cliente de login do Google
    private lateinit var auth: FirebaseAuth // Instância do Firebase Auth

    private val RC_SIGN_IN = 1001 // Código de requisição para identificar o retorno do login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o binding com o layout da tela
        binding = ActivityLoginGoogleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa o Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configura o Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // ID do cliente Web (SHA + projeto Firebase)
            .requestEmail() // Solicita o e-mail do usuário
            .build()

        // Cria o cliente de login com Google baseado nas configurações acima
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Ação do botão: iniciar o fluxo de login com Google
        binding.btnGoogleSignIn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    // Recebe o resultado da intent de login
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Verifica se o resultado veio da requisição de login com Google
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Tenta obter os dados da conta Google
                val account = task.getResult(ApiException::class.java)!!
                // Envia o token da conta para o Firebase
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Se falhar, exibe erro
                Toast.makeText(this, "Erro no login: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Faz o login com Firebase usando a conta do Google
    private fun firebaseAuthWithGoogle(idToken: String) {
        // Cria credencial do Firebase usando o token da conta Google
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        // Tenta autenticar no Firebase com a credencial
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Se deu certo, pega o usuário e exibe uma mensagem
                    val user = auth.currentUser
                    Toast.makeText(this, "Bem-vindo, ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    finish() // Fecha a activity e volta
                } else {
                    // Se falhou, exibe mensagem de erro
                    Toast.makeText(this, "Falha na autenticação.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
