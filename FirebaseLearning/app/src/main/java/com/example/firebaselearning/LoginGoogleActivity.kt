package com.example.firebaselearning

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaselearning.databinding.ActivityLoginGoogleBinding
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*

class LoginGoogleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginGoogleBinding // ViewBinding para acessar os componentes do layout
    private lateinit var googleSignInClient: GoogleSignInClient // Cliente de login com Google
    private lateinit var auth: FirebaseAuth // Instância do Firebase Auth

    private val RC_SIGN_IN = 1001 // Código de requisição para identificar o retorno do login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla o layout usando ViewBinding
        binding = ActivityLoginGoogleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ativa o botão de voltar na ActionBar padrão
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Login com Google"

        // Inicializa o Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Configura as opções de login com Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Token necessário para autenticar no Firebase
            .requestEmail() // Solicita o e-mail do usuário
            .build()

        // Cria o cliente de login com base nas opções definidas
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Verifica se há um usuário logado
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // Se o usuário estiver logado, exibe as informações dele

            var info: String

            // Se displayName for nulo ou vazio, usamos o email como nome
            if (!currentUser.displayName.isNullOrBlank()) {
                info = "Usuário: ${currentUser.displayName}\n" +"Email: ${currentUser.email}"
            } else {
                info = "Usuário: ${currentUser.email}"
            }

            binding.userInfoTextView.text = info
            binding.userInfoTextView.visibility = android.view.View.VISIBLE

            // Esconde botão de login e mostra botão de logout
            binding.btnGoogleSignIn.visibility = android.view.View.GONE
            binding.btnGoogleSignOut.visibility = android.view.View.VISIBLE
        } else {
            // Se não houver usuário logado, exibe apenas o botão de login
            binding.userInfoTextView.visibility = android.view.View.GONE
            binding.btnGoogleSignIn.visibility = android.view.View.VISIBLE
            binding.btnGoogleSignOut.visibility = android.view.View.GONE
        }

        // Quando o botão de login é clicado, inicia o processo de login com Google
        binding.btnGoogleSignIn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        // Quando o botão de logout é clicado, faz logout do Firebase e Google
        binding.btnGoogleSignOut.setOnClickListener {
            auth.signOut() // Firebase

            val googleAccount = GoogleSignIn.getLastSignedInAccount(this)
            if (googleAccount != null) {
                googleSignInClient.signOut().addOnCompleteListener {
                    Toast.makeText(this, "Logout realizado com sucesso", Toast.LENGTH_SHORT).show()
                    recreate()
                }
            } else {
                Toast.makeText(this, "Logout realizado com sucesso", Toast.LENGTH_SHORT).show()
                recreate()
            }
        }


        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // Fecha a activity e volta à anterior
        return true
    }

    // Recebe o resultado do login com Google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Verifica se o resultado é da tentativa de login
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Se obteve sucesso ao recuperar a conta, autentica com Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Se falhar, exibe mensagem de erro
                Toast.makeText(this, "Erro no login: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Autentica o usuário no Firebase com credencial Google
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Bem-vindo, ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    recreate() // Atualiza a tela para mostrar os dados do usuário
                } else {
                    Toast.makeText(this, "Falha na autenticação.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
