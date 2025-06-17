package com.example.firebaselearning

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaselearning.databinding.ActivityMoodListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Activity responsável por exibir a lista de humores cadastrados no Firestore
class MoodListActivity : AppCompatActivity() {

    // ViewBinding para acessar os componentes da tela
    private lateinit var binding: ActivityMoodListBinding

    // Instância do Firestore para acessar o banco de dados
    private val firestore = FirebaseFirestore.getInstance()

    // Lista que armazenará os dados dos humores buscados do Firestore
    private val moods = mutableListOf<Mood>()

    // Adaptador do RecyclerView que mostrará os dados na tela
    private val adapter = MoodAdapter(moods)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoodListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (FirebaseAuth.getInstance().currentUser == null) {
            Toast.makeText(this, "Faça login para ver seus humores", Toast.LENGTH_SHORT).show()
            finish() // Fecha essa tela se não estiver logado
            return
        }

        // Configura o RecyclerView com layout em lista vertical
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Carrega os humores do Firestore
        carregarHumores()
    }

    // Função que busca os dados da coleção "humores" no Firestore
    private fun carregarHumores() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "Usuário não está logado!", Toast.LENGTH_SHORT).show()
            finish() // ou redirecionar para login
            return
        }

        firestore.collection("humores")
            .whereEqualTo("uid", uid) // 🔐 Só os do usuário
            .get()
            .addOnSuccessListener { result ->
                // Limpa a lista antes de preencher com novos dados
                moods.clear()
                for (doc in result) {
                    // Converte cada documento em um objeto Mood
                    val mood = Mood(
                        data = doc.getString("data") ?: "",
                        humor = doc.getString("humor") ?: "",
                        cor = (doc.get("cor") as? Long)?.toInt() ?: Color.LTGRAY
                    )
                    // Adiciona o humor à lista
                    moods.add(mood)
                }
                // Notifica o adaptador para atualizar o RecyclerView
                adapter.notifyDataSetChanged()
            }
    }
}
