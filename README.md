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

<h2>Amarrando dados ao usuário autenticado no Firebase (com exemplo de implementação)</h2>

<p>Este passo a passo mostra como salvar e ler dados no Firestore vinculando-os ao usuário autenticado via Firebase Authentication (ex: login com Google).</p>

<h3> 1. Salvar dados com o UID do usuário</h3>
<p>Ao salvar dados no Firestore, associe o <code>uid</code> do usuário autenticado:</p>

<pre><code class="language-kotlin">
val uid = FirebaseAuth.getInstance().currentUser?.uid

if (uid == null) {
    Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
    return
}

val moodData = hashMapOf(
    "data" to selectedDate,
    "humor" to selectedMoodText,
    "cor" to selectedMoodColor,
    "uid" to uid // Amarra o dado ao usuário
)

FirebaseFirestore.getInstance().collection("humores")
    .add(moodData)
    .addOnSuccessListener {
        Toast.makeText(this, "Humor salvo com sucesso!", Toast.LENGTH_SHORT).show()
    }
    .addOnFailureListener {
        Toast.makeText(this, "Erro ao salvar humor.", Toast.LENGTH_SHORT).show()
    }
</code></pre>

<h3> 2. Ler apenas os dados do usuário logado</h3>
<p>Filtre os dados usando o mesmo <code>uid</code> ao fazer a leitura:</p>

<pre><code class="language-kotlin">
val uid = FirebaseAuth.getInstance().currentUser?.uid

if (uid == null) {
    Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
    finish()
    return
}

FirebaseFirestore.getInstance().collection("humores")
    .whereEqualTo("uid", uid)
    .get()
    .addOnSuccessListener { result ->
        for (document in result) {
            val data = document.getString("data")
            val humor = document.getString("humor")
            val cor = document.getLong("cor")?.toInt()

            // Adicione à lista/RecyclerView conforme sua lógica
        }
    }
    .addOnFailureListener {
        Toast.makeText(this, "Erro ao carregar humores.", Toast.LENGTH_SHORT).show()
    }
</code></pre>

<h3> 3. Regras de segurança no Firestore</h3>
<p>Garanta que um usuário só possa ler/escrever os próprios dados no Firestore:</p>

<pre><code class="language-js">
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /humores/{document} {
      allow read, write: if request.auth != null && request.auth.uid == resource.data.uid;
    }
  }
}
</code></pre>

<p> Essas regras exigem que o campo <code>uid</code> esteja presente em cada documento.</p>

<h3> Resumo</h3>
<ul>
  <li>Obtenha o <code>uid</code> do usuário autenticado:
    <pre><code class="language-kotlin">FirebaseAuth.getInstance().currentUser?.uid</code></pre>
  </li>
  <li>Salve esse <code>uid</code> no documento no Firestore</li>
  <li>Filtre os dados usando <code>.whereEqualTo("uid", uid)</code></li>
  <li>Proteja os dados com regras de segurança no Firestore</li>
</ul>

<h2>Login com E-mail e Senha no Firebase (com exemplo de implementação)</h2>

<h3>1. Ative o Login com E-mail/Senha no Firebase</h3>
<ul>
  <li>Vá no <a href="https://console.firebase.google.com/" target="_blank">Firebase Console</a></li>
  <li>Selecione seu projeto</li>
  <li>Vá até <strong>Authentication &gt; Método de login</strong></li>
  <li>Ative o <strong>E-mail/Senha</strong> e clique em <strong>Salvar</strong></li>
</ul>

<h3>2. Adicione as dependências do Firebase Authentication</h3>
<p>No <code>build.gradle (app)</code>:</p>
<pre><code class="language-kotlin">
implementation 'com.google.firebase:firebase-auth:22.3.1'
</code></pre>
<p>Certifique-se de já ter o <code>google-services.json</code> e o plugin:</p>
<pre><code class="language-kotlin">
apply plugin: 'com.google.gms.google-services'
</code></pre>

<h3>3. Crie o layout <code>activity_login.xml</code></h3>
<pre><code class="language-xml">
&lt;LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:padding="16dp"
    android:layout_width="match_parent" android:layout_height="match_parent"&gt;

    &lt;EditText
        android:id="@+id/etEmail"
        android:hint="Email"
        android:inputType="textEmailAddress"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/&gt;

    &lt;EditText
        android:id="@+id/etSenha"
        android:hint="Senha"
        android:inputType="textPassword"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/&gt;

    &lt;Button
        android:id="@+id/btnLogin"
        android:text="Login"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/&gt;

    &lt;Button
        android:id="@+id/btnCadastrar"
        android:text="Cadastrar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/&gt;

&lt;/LinearLayout&gt;
</code></pre>

<h3>4. Implemente a <code>LoginEmailActivity.kt</code></h3>
<pre><code class="language-kotlin">
class LoginEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val senha = binding.etSenha.text.toString()
            auth.signInWithEmailAndPassword(email, senha)
                .addOnSuccessListener {
                    Toast.makeText(this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro no login: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnCadastrar.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val senha = binding.etSenha.text.toString()
            auth.createUserWithEmailAndPassword(email, senha)
                .addOnSuccessListener {
                    Toast.makeText(this, "Cadastro feito com sucesso", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro no cadastro: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
</code></pre>

<h3>5. Usar o usuário autenticado no app</h3>
<p>Após login ou cadastro, você pode obter o usuário atual com:</p>
<pre><code class="language-kotlin">
val usuario = FirebaseAuth.getInstance().currentUser
val uid = usuario?.uid
</code></pre>
<p>Com esse <code>uid</code>, você pode salvar dados no Firestore ou Realtime Database de forma segura e individual para cada usuário.</p>

<h2> Reset de Senha com Firebase Authentication</h2>

<h3>1. Adicione um botão "Esqueceu a senha?" no layout</h3>
<pre><code class="language-xml">
&lt;TextView
    android:id="@+id/tvEsqueceuSenha"
    android:text="Esqueceu a senha?"
    android:textColor="@android:color/holo_blue_dark"
    android:layout_marginTop="8dp"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" /&gt;
</code></pre>

<h3>2. Enviar o e-mail de redefinição de senha no Kotlin</h3>
<pre><code class="language-kotlin">
binding.tvEsqueceuSenha.setOnClickListener {
    val email = binding.etEmail.text.toString().trim()
    if (email.isNotEmpty()) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnSuccessListener {
                Toast.makeText(this, "E-mail de redefinição enviado!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    } else {
        Toast.makeText(this, "Digite seu e-mail para redefinir a senha", Toast.LENGTH_SHORT).show()
    }
}
</code></pre>

<h3>3. Personalizar o conteúdo dos e-mails</h3>
<ul>
  <li>Volte ao <a href="https://console.firebase.google.com/" target="_blank">Firebase Console</a></li>
  <li>Vá em <strong>Authentication &gt; Modelos de e-mail</strong></li>
  <li>Clique em <strong>Redefinição de senha</strong></li>
  <li>Edite o <strong>assunto</strong> e o <strong>corpo do e-mail</strong></li>
  <li>Use variáveis como <code>{{email}}</code> e <code>{{url}}</code></li>
  <li>Salve as alterações</li>
</ul>

<h3>4. (Opcional) Usar domínio personalizado nos links de e-mail</h3>
<ul>
  <li>No Firebase, clique em <strong>Adicionar domínio personalizado</strong></li>
  <li>Informe o domínio (ex: auth.seusite.com)</li>
  <li>Adicione o registro TXT indicado no seu provedor de DNS</li>
  <li>Depois da propagação, clique em <strong>Verificar</strong></li>
</ul>
<p>Assim, os links enviados nos e-mails virão de <code>auth.seusite.com</code> ao invés de <code>firebaseapp.com</code></p>

<h3>5. (Avançado) Redirecionar para uma URL ou tela no app</h3>
<p>Você pode configurar a URL de redirecionamento após redefinir a senha com <code>ActionCodeSettings</code>:</p>
<pre><code class="language-kotlin">
val actionCodeSettings = ActionCodeSettings.newBuilder()
    .setUrl("https://seusite.com/resetado")
    .setHandleCodeInApp(true)
    .setAndroidPackageName("com.seuprojeto.app", true, null)
    .build()

FirebaseAuth.getInstance().sendPasswordResetEmail(email, actionCodeSettings)
</code></pre>

<h2> Verificação de E-mail com Firebase Authentication</h2>

<h3>1. Enviar e-mail de verificação após o registro</h3>
<pre><code class="language-kotlin">
FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha)
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val usuario = FirebaseAuth.getInstance().currentUser
            usuario?.sendEmailVerification()
                ?.addOnSuccessListener {
                    Toast.makeText(this, "Verifique seu e-mail para ativar a conta!", Toast.LENGTH_LONG).show()
                }
                ?.addOnFailureListener {
                    Toast.makeText(this, "Erro ao enviar verificação: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
</code></pre>

<h3>2. Verificar se o e-mail foi confirmado antes de permitir login</h3>
<pre><code class="language-kotlin">
val user = FirebaseAuth.getInstance().currentUser
if (user != null && user.isEmailVerified) {
    // Login permitido
    startActivity(Intent(this, MainActivity::class.java))
} else {
    // Bloqueia o acesso e desloga
    FirebaseAuth.getInstance().signOut()
    Toast.makeText(this, "Verifique seu e-mail antes de fazer login.", Toast.LENGTH_LONG).show()
}
</code></pre>

<h3>3. Reenviar o e-mail de verificação (opcional)</h3>
<pre><code class="language-kotlin">
FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
    ?.addOnSuccessListener {
        Toast.makeText(this, "E-mail de verificação reenviado!", Toast.LENGTH_SHORT).show()
    }
    ?.addOnFailureListener {
        Toast.makeText(this, "Erro: ${it.message}", Toast.LENGTH_SHORT).show()
    }
</code></pre>

<h3>4. Personalizar o conteúdo do e-mail</h3>
<ul>
  <li>Vá para o <a href="https://console.firebase.google.com/" target="_blank">Firebase Console</a></li>
  <li>Abra <strong>Authentication &gt; Modelos de e-mail</strong></li>
  <li>Clique em <strong>Verificação de e-mail</strong></li>
  <li>Edite o <strong>assunto</strong> e o <strong>corpo do e-mail</strong></li>
  <li>Você pode usar variáveis como:
    <ul>
      <li><code>{{email}}</code> – E-mail do usuário</li>
      <li><code>{{url}}</code> – Link de verificação</li>
    </ul>
  </li>
  <li>Clique em <strong>Salvar</strong></li>
</ul>

<h3>5. (Opcional) Usar domínio personalizado nos links</h3>
<ul>
  <li>No Firebase, clique em <strong>Adicionar domínio personalizado</strong></li>
  <li>Informe seu domínio (ex: auth.seusite.com)</li>
  <li>Adicione o registro TXT no DNS do seu domínio</li>
  <li>Aguarde a propagação e clique em <strong>Verificar</strong></li>
</ul>
<p>Isso fará com que os links enviados nos e-mails venham de <code>auth.seusite.com</code> em vez de <code>firebaseapp.com</code></p>

<h3>6. (Avançado) Redirecionar para uma URL ou tela no app</h3>
<p>Você pode personalizar para onde o usuário será levado após confirmar o e-mail, usando <code>ActionCodeSettings</code>:</p>
<pre><code class="language-kotlin">
val actionCodeSettings = ActionCodeSettings.newBuilder()
    .setUrl("https://seusite.com/confirmado")
    .setHandleCodeInApp(true)
    .setAndroidPackageName("com.seuprojeto.app", true, null)
    .build()

FirebaseAuth.getInstance().currentUser?.sendEmailVerification(actionCodeSettings)
</code></pre>

