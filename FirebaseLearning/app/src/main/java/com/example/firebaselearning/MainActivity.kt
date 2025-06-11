package com.example.firebaselearning

import android.content.Intent
import android.os.Bundle
import android.widget.GridLayout
import android.widget.Toast
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


    }

    private fun adicionarModulo(modulo: Modulo) {
        val itemBinding = ItemModuloBinding.inflate(layoutInflater)

        itemBinding.txtTituloModulo.text = modulo.title
        itemBinding.imgIcone.setImageResource(modulo.icon)

        itemBinding.cardModulo.setOnClickListener {
            modulo.onClick()
        }

        val layoutParams = GridLayout.LayoutParams().apply {
            width = 0
            height = GridLayout.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
        }

        itemBinding.root.layoutParams = layoutParams
        binding.gridLayout.addView(itemBinding.root)
    }


}


