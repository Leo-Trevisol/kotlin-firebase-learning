package com.example.firebaselearning

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaselearning.databinding.ActivityMoodListBinding
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

        // Configura o RecyclerView com layout em lista vertical
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Carrega os humores do Firestore
        carregarHumores()
    }

    // Função que busca os dados da coleção "humores" no Firestore
    private fun carregarHumores() {
        firestore.collection("humores")
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
