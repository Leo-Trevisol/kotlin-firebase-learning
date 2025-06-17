package com.example.firebaselearning

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaselearning.databinding.ActivityRegisterMoodBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class RegisterMoodActivity : AppCompatActivity() {

    // View binding para acessar elementos da UI
    private lateinit var binding: ActivityRegisterMoodBinding

    // Variáveis para armazenar os dados selecionados pelo usuário
    private var selectedDate: String = ""
    private var selectedMoodColor: Int = Color.YELLOW
    private var selectedMoodText: String = "Feliz"

    // Lista de humores disponíveis com suas cores associadas
    private val moodList = listOf(
        "Feliz" to Color.YELLOW,
        "Triste" to Color.BLUE,
        "Ansioso" to Color.GRAY,
        "Neutro" to Color.LTGRAY,
        "Irritado" to Color.RED
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterMoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura os elementos da tela
        configurarSpinner()
        configurarSelecionarData()

        // Botão para salvar o humor no Firestore
        binding.btnCadastrarHumor.setOnClickListener {
            salvarHumor()
        }

        // Botão para abrir a lista de humores cadastrados
        binding.btnVerHumores.setOnClickListener {
            // Obtém o usuário atualmente autenticado no Firebase
            val user = FirebaseAuth.getInstance().currentUser

            // Verifica se o usuário está logado
            if (user == null) {
                Toast.makeText(this, "Usuário não está logado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Obtém a instância do Firestore
            val db = FirebaseFirestore.getInstance()

            // Consulta a coleção "humores" filtrando pelos documentos que pertencem ao usuário atual (campo "uid")
            db.collection("humores")
                .whereEqualTo("uid", user.uid)
                .get()
                .addOnSuccessListener { documents ->
                    // Se não houver nenhum documento (humor) cadastrado para o usuário
                    if (documents.isEmpty) {
                        Toast.makeText(this, "Nenhum humor cadastrado ainda", Toast.LENGTH_SHORT).show()
                    } else {
                        // Caso existam humores cadastrados, abre a tela de lista de humores
                        startActivity(Intent(this, MoodListActivity::class.java))
                    }
                }
                .addOnFailureListener { e ->
                    // Caso ocorra algum erro ao consultar o Firestore, mostra uma mensagem de erro
                    Toast.makeText(this, "Erro ao consultar humores: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    // Função para configurar a seleção de data
    private fun configurarSelecionarData() {
        val calendar = Calendar.getInstance()

        // Cria um DatePickerDialog para o usuário escolher a data
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate = "$dayOfMonth/${month + 1}/$year"
                binding.txtSelectedDate.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Abre o seletor de data ao clicar no campo de texto
        binding.txtSelectedDate.setOnClickListener {
            datePicker.show()
        }
    }

    // Função para configurar o spinner de humor
    private fun configurarSpinner() {
        val moods = moodList.map { it.first } // Só os textos
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, moods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerHumor.adapter = adapter

        // Inicializa com o primeiro item selecionado
        binding.spinnerHumor.setSelection(0)
        selectedMoodText = moodList[0].first
        selectedMoodColor = moodList[0].second

        // Detecta quando o usuário seleciona um item
        binding.spinnerHumor.setOnItemSelectedListener { _, _, position, _ ->
            selectedMoodText = moodList[position].first
            selectedMoodColor = moodList[position].second

            // Atualiza a cor de fundo do spinner (opcional)
            binding.spinnerHumor.setBackgroundColor(selectedMoodColor ?: Color.LTGRAY)
        }
    }

    // Função para salvar o humor no Firestore
    private fun salvarHumor() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        // Verifica se o usuário está autenticado
        if (uid == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        // Verifica se o usuário selecionou uma data
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Selecione uma data", Toast.LENGTH_SHORT).show()
            return
        }

        // Cria um mapa com os dados do humor
        val moodData = hashMapOf(
            "data" to selectedDate,
            "humor" to selectedMoodText,
            "cor" to selectedMoodColor,
            "uid" to uid,
            "timestamp" to Timestamp.now() // ajuda a ordenar os dados
        )

        // Envia os dados para a coleção "humores" no Firestore
        FirebaseFirestore.getInstance()
            .collection("humores")
            .add(moodData)
            .addOnSuccessListener {
                Toast.makeText(this, "Humor salvo com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar humor: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Função de extensão para configurar listener no spinner de forma mais simples
    private fun android.widget.Spinner.setOnItemSelectedListener(listener: (parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) -> Unit) {
        this.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                listener(parent, view, position, id)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }
    }
}
