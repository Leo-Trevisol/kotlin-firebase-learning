package com.example.firebaselearning

import android.content.Intent
import android.os.Bundle
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.firebaselearning.databinding.ActivityMainBinding
import com.example.firebaselearning.databinding.ItemModuloBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adicionarModulo(
            Modulo(
                icon = R.drawable.ic_google,
                title = "Login com Google"
            ) {
                val intent = Intent(this, LoginGoogleActivity::class.java)
                startActivity(intent)
            }
        )

        adicionarModulo(
            Modulo(
                icon = R.drawable.ic_user,
                title = "Login com Usuário e Senha"
            ) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        )

        adicionarModulo(
            Modulo(
                icon = R.drawable.ic_mood,
                title = "Cadastrar humor"
            ) {
                val intent = Intent(this, RegisterMoodActivity::class.java)
                startActivity(intent)
            }
        )

    }

    private fun adicionarModulo(modulo: Modulo) {
        val itemBinding = ItemModuloBinding.inflate(layoutInflater)

        itemBinding.txtTituloModulo.text = modulo.title
        itemBinding.imgIcone.setImageResource(modulo.icon)

        itemBinding.cardModulo.setOnClickListener {
            modulo.onClick()
        }

        // Remove o uso de peso e largura 0dp
        val layoutParams = GridLayout.LayoutParams().apply {
            width = GridLayout.LayoutParams.MATCH_PARENT
            height = GridLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED)
        }

        itemBinding.root.layoutParams = layoutParams
        binding.gridLayout.addView(itemBinding.root)
    }



}


