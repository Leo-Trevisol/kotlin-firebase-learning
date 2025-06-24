package com.example.firebaselearning

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaselearning.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    // ViewBinding para acessar os elementos da tela
    private lateinit var binding: ActivityProfileBinding

    // Referência do Realtime Database do Firebase
    private lateinit var database: DatabaseReference

    // Instância do FirebaseAuth para autenticação
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla o layout usando ViewBinding
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa a referência raiz do Realtime Database
        database = FirebaseDatabase.getInstance().reference

        // Obtém o usuário atualmente autenticado
        val user = auth.currentUser

        if (user != null) {
            val uid = user.uid  // UID exclusivo do usuário autenticado

            // 🔥 Lê o nome salvo no Realtime Database ao abrir a tela
            database.child("usuarios").child(uid).child("nome")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Obtém o valor do nome ou retorna vazio se não existir
                        val nome = snapshot.getValue(String::class.java)
                        binding.edtNome.setText(nome ?: "")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Exibe erro caso a leitura falhe (ex: falta de permissão)
                        Toast.makeText(this@ProfileActivity, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
                    }
                })

            // 📝 Botão para salvar/atualizar o nome no banco
            binding.btnSalvar.setOnClickListener {
                val nomeDigitado = binding.edtNome.text.toString().trim()

                if (nomeDigitado.isNotEmpty()) {
                    // Salva o nome dentro do nó "usuarios/{uid}/nome"
                    database.child("usuarios").child(uid).child("nome").setValue(nomeDigitado)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Nome salvo com sucesso!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Digite um nome", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Caso o usuário não esteja autenticado, exibe aviso e fecha a tela
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
