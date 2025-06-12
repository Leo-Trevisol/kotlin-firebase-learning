package com.example.firebaselearning

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaselearning.databinding.ActivityRegisterMoodBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class RegisterMoodActivity : AppCompatActivity() {

    // ViewBinding para acessar os componentes da tela
    private lateinit var binding: ActivityRegisterMoodBinding

    // Instância do Firestore
    private val firestore = FirebaseFirestore.getInstance()

    // Lista de humores disponíveis com suas cores associadas
    private val moodList = listOf(
        "Feliz" to Color.YELLOW,
        "Triste" to Color.BLUE,
        "Ansioso" to Color.GRAY,
        "Neutro" to Color.LTGRAY,
        "Irritado" to Color.RED
    )

    // Variáveis para armazenar o humor e a data selecionados
    private var selectedDate: String? = null
    private var selectedMoodColor: Int? = null
    private var selectedMoodText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterMoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura os componentes da interface
        configurarData()
        configurarSpinner()
        configurarBotoes()
    }

    // Abre um DatePicker para o usuário selecionar a data
    private fun configurarData() {
        val calendar = Calendar.getInstance()
        binding.txtSelectedDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    // Formata e salva a data selecionada
                    selectedDate = "$dayOfMonth/${month + 1}/$year"
                    binding.txtSelectedDate.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    // Configura o spinner com os humores disponíveis
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

    // Configura os botões de ação
    private fun configurarBotoes() {
        // Quando o botão de "Cadastrar" é clicado
        binding.btnCadastrarHumor.setOnClickListener {
            if (selectedDate == null) {
                Toast.makeText(this, "Por favor, selecione uma data.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cria um objeto com os dados para salvar no Firestore
            val moodData = hashMapOf(
                "data" to selectedDate,
                "humor" to selectedMoodText,
                "cor" to selectedMoodColor
            )

            // Salva o humor na coleção "humores"
            firestore.collection("humores")
                .add(moodData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Humor salvo com sucesso!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao salvar humor.", Toast.LENGTH_SHORT).show()
                }
        }

        // Quando o botão de "Ver humores cadastrados" é clicado
        binding.btnVerHumores.setOnClickListener {
            Toast.makeText(this, "Abrindo lista de humores cadastrados...", Toast.LENGTH_SHORT).show()
            // Aqui você pode iniciar uma nova activity no futuro
        }
    }

    // Extensão que facilita o uso do setOnItemSelectedListener para Spinners
    private fun View.setOnItemSelectedListener(
        listener: (parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) -> Unit
    ) {
        if (this is android.widget.Spinner) {
            this.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                    listener(parent, view, position, id)
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
            }
        }
    }
}
