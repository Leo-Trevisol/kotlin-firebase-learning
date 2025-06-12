# firebase-learning
Repositório para estudos e experimentos com Firebase, incluindo autenticação, banco de dados, e funções em nuvem


### 1. Criar projeto no Firebase Console

1. Vá para [Firebase Console](https://console.firebase.google.com/)
2. Clique em **"Adicionar projeto"**
3. Siga os passos (não precisa ativar o Google Analytics)
4. Após criado, clique em **“Adicionar app” → Android**
5. Insira o nome do pacote do seu app (ex: `com.seuapp.exemplo`)
6. (Opcional) Insira apelido e SHA-1 (para login com Google)
7. Baixe o arquivo `google-services.json`
8. Coloque o `google-services.json` na pasta `app/` do seu projeto Android

<h3> 2. Configurar arquivos do projeto (Gradle)</h3>

<p><strong><code>build.gradle</code> (projeto - nível superior)</strong></p>

<pre><code>buildscript {
    dependencies {
        classpath 'com.google.gms:google-services:4.4.1' // Última versão
    }
}
</code></pre>

<p><strong><code>build.gradle</code> (app - nível inferior)</strong></p>

<pre><code>plugins {
    id 'com.android.application'
    id 'com.google.gms.google-services' // Adicione esta linha
}

android {
    // ...
}

dependencies {
    implementation platform('com.google.firebase:firebase-bom:32.7.4') // Atualize conforme necessário

    // Autenticação
    implementation 'com.google.firebase:firebase-auth-ktx'

    // Firestore
    implementation 'com.google.firebase:firebase-firestore-ktx'
}
</code></pre>

<h3> 3. Inicializar o Firebase no App</h3>

<p>No seu <code>MainActivity.kt</code> (ou em uma classe <code>Application</code>), adicione:</p>

<pre><code class="language-kotlin">
import com.google.firebase.FirebaseApp

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    FirebaseApp.initializeApp(this)
}
</code></pre>

<p>Ou, se estiver usando uma classe <code>Application</code> personalizada:</p>

<pre><code class="language-kotlin">
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
</code></pre>

<p>E registre no <code>AndroidManifest.xml</code>:</p>

<pre><code class="language-xml">
&lt;application
    android:name=".MyApp"
    ...&gt;
</code></pre>

<h3> 4. Usar Firebase Auth (Autenticação)</h3>

<p><strong>Criar novo usuário (email/senha):</strong></p>

<pre><code class="language-kotlin">
FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val user = FirebaseAuth.getInstance().currentUser
            Log.d("AUTH", "Usuário criado: ${user?.email}")
        } else {
            Log.e("AUTH", "Erro: ${task.exception?.message}")
        }
    }
</code></pre>

<p><strong>Fazer login:</strong></p>

<pre><code class="language-kotlin">
FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha)
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Log.d("AUTH", "Login bem-sucedido!")
        } else {
            Log.e("AUTH", "Erro no login: ${task.exception?.message}")
        }
    }
</code></pre>

<h3> 5. Usar Firestore</h3>

<p><strong>Obter instância:</strong></p>

<pre><code class="language-kotlin">
val db = FirebaseFirestore.getInstance()
</code></pre>

<p><strong>Salvar dados:</strong></p>

<pre><code class="language-kotlin">
val dados = hashMapOf(
    "nome" to "João",
    "idade" to 22
)

db.collection("usuarios").document("usuario1")
    .set(dados)
    .addOnSuccessListener {
        Log.d("FIRESTORE", "Dados salvos com sucesso!")
    }
    .addOnFailureListener {
        Log.e("FIRESTORE", "Erro ao salvar: ${it.message}")
    }
</code></pre>

<p><strong>Ler dados:</strong></p>

<pre><code class="language-kotlin">
db.collection("usuarios").document("usuario1")
    .get()
    .addOnSuccessListener { document ->
        if (document != null) {
            val nome = document.getString("nome")
            Log.d("FIRESTORE", "Nome: $nome")
        }
    }
</code></pre>

<h2> Login com Google no Firebase (com exemplo de implementação)</h2>

<h3> 1. Ative o Login com Google no Firebase</h3>
<ul>
  <li>Vá no <a href="https://console.firebase.google.com/" target="_blank">Firebase Console</a></li>
  <li>Selecione seu projeto</li>
  <li>Vá até <strong>Authentication &gt; Método de login</strong></li>
  <li>Ative o <strong>Google</strong> e clique em <strong>Salvar</strong></li>
</ul>

<h3> 2. Obtenha o SHA-1 e adicione no Firebase</h3>
<p>No terminal, dentro da raiz do seu projeto Android, execute:</p>

<pre><code class="language-bash">./gradlew signingReport</code></pre>

<p>Copie o <strong>SHA-1</strong> (e <strong>SHA-256</strong>, se quiser).</p>
<p>No Firebase Console:</p>
<ul>
  <li>Vá em <strong>Configurações do Projeto</strong> (ícone de engrenagem)</li>
  <li>Clique no app Android registrado</li>
  <li>Clique em <strong>“Adicionar SHA-1”</strong></li>
  <li>Cole o valor e salve</li>
  <li><strong>Após isso, baixe o novo <code>google-services.json</code> e substitua no seu projeto</strong></li>
</ul>

<h3> 3. Adicione a dependência de login com Google</h3>
<p>No <code>build.gradle (app)</code>:</p>

<pre><code class="language-kotlin">implementation 'com.google.android.gms:play-services-auth:21.0.0'</code></pre>
<p><em>(As demais dependências do Firebase você já deve ter)</em></p>

<h3> 4. Adicione o <code>client_id</code> ao <code>strings.xml</code></h3>
<p>No <code>google-services.json</code>, ache <code>"client_type": 3</code> e copie o <code>client_id</code>:</p>

<pre><code class="language-xml">
&lt;string name="default_web_client_id"&gt;xxxxxxxxxxx.apps.googleusercontent.com&lt;/string&gt;
</code></pre>

<h3> 5. Implementar a <code>LoginGoogleActivity</code> de exemplo</h3>

<pre><code class="language-kotlin">
class LoginGoogleActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 9001
    private lateinit var binding: ActivityLoginGoogleBinding

    private val googleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(this, gso)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginGoogleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoogleSignIn.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Olá, ${it.user?.displayName}", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro no login", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: Exception) {
                Toast.makeText(this, "Falha: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
</code></pre>

<h2> Cloud Firestore no Firebase (com exemplo de implementação)</h2>

<h3>1. Ative o Firestore no Firebase</h3>
<ul>
  <li>Vá no <a href="https://console.firebase.google.com/" target="_blank">Firebase Console</a></li>
  <li>Selecione seu projeto</li>
  <li>No menu lateral, clique em <strong>Firestore Database</strong></li>
  <li>Clique em <strong>"Criar banco de dados"</strong></li>
  <li>Escolha o <strong>modo de teste</strong> para desenvolvimento:</li>
</ul>

<pre><code class="language-js">rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true;
    }
  }
}</code></pre>

<ul>
  <li>Escolha uma região, como <code>southamerica-east1</code> para o Brasil</li>
  <li>Clique em <strong>Ativar</strong></li>
</ul>

<h3>2. Estrutura de exemplo</h3>
<ul>
  <li><strong>Coleção:</strong> <code>humores</code></li>
  <li><strong>Campos de cada documento:</strong>
    <ul>
      <li><code>data</code> – data do humor (ex: "11/06/2025")</li>
      <li><code>humor</code> – texto do humor (ex: "Feliz")</li>
      <li><code>cor</code> – valor inteiro da cor (ex: -256)</li>
    </ul>
  </li>
</ul>

<h3>3. Exemplo de código Kotlin para salvar no Firestore</h3>
<pre><code class="language-kotlin">
val firestore = FirebaseFirestore.getInstance()

val moodData = hashMapOf(
    "data" to selectedDate,
    "humor" to selectedMoodText,
    "cor" to selectedMoodColor
)

firestore.collection("humores")
    .add(moodData)
    .addOnSuccessListener {
        Toast.makeText(this, "Humor salvo com sucesso!", Toast.LENGTH_SHORT).show()
    }
    .addOnFailureListener {
        Toast.makeText(this, "Erro ao salvar humor.", Toast.LENGTH_SHORT).show()
    }
</code></pre>

<h3>4. Regras de segurança para produção (opcional)</h3>
<p>Quando for publicar seu app, troque as regras para permitir acesso apenas a usuários autenticados:</p>

<pre><code class="language-js">rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /humores/{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}</code></pre>




