package com.example.firebaselearning

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaselearning.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

/**
 * ChatActivity demonstra um chat simples usando Firebase Realtime Database.
 * As mensagens são sincronizadas em tempo real entre todos os dispositivos conectados.
 */
class ChatActivity : AppCompatActivity() {

    // ViewBinding para acessar os elementos da interface de forma segura
    private lateinit var binding: ActivityChatBinding

    // Referência ao nó "messages" do Realtime Database
    private lateinit var database: DatabaseReference

    // Lista que armazena as mensagens do chat em memória
    private val mensagens = mutableListOf<Mensagem>()

    // Adapter responsável por exibir as mensagens no RecyclerView
    private lateinit var adapter: MensagemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa a referência ao nó "messages" no banco de dados
        database = FirebaseDatabase.getInstance().getReference("messages")

        // Configura o RecyclerView com layout vertical e conecta o adapter
        adapter = MensagemAdapter(mensagens)
        binding.rvMensagens.layoutManager = LinearLayoutManager(this)
        binding.rvMensagens.adapter = adapter

        // Escuta o Realtime Database para receber novas mensagens em tempo real
        database.addChildEventListener(object : ChildEventListener {

            // Método chamado sempre que uma nova mensagem é adicionada ao banco
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val mensagem = snapshot.getValue(Mensagem::class.java)
                if (mensagem != null) {
                    mensagens.add(mensagem) // Adiciona à lista local
                    adapter.notifyItemInserted(mensagens.size - 1) // Atualiza o RecyclerView
                    binding.rvMensagens.scrollToPosition(mensagens.size - 1) // Rola para a última mensagem
                }
            }

            // Métodos não utilizados, mas obrigatórios de implementar
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            // Tratamento de erro caso algo dê errado ao ouvir o banco
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity, "Erro: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Evento de clique no botão de enviar mensagem
        binding.btnEnviar.setOnClickListener {
            val texto = binding.edtMensagem.text.toString().trim()

            if (texto.isNotEmpty()) {
                val autor = FirebaseAuth.getInstance().currentUser?.email ?: "Anônimo"

                // Cria o objeto da mensagem com o texto e o autor
                val mensagem = Mensagem(texto, autor)

                // Envia a mensagem para o Realtime Database
                database.push().setValue(mensagem)

                // Limpa o campo de texto após o envio
                binding.edtMensagem.text.clear()
            }
        }
    }
}
