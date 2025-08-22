package com.example.ediglobe_task

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ediglobe_task.data.model.Task
import com.example.ediglobe_task.databinding.ActivityMainBinding
import com.example.ediglobe_task.ui.adapter.TaskAdapter
import com.example.ediglobe_task.ui.viewmodel.TaskViewModel
import com.example.ediglobe_task.ui.viewmodel.TaskViewModelFactory
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.logging.Log

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var loginButton: Button
    private lateinit var auth: FirebaseAuth

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        setupToolbar()
        setupViewModel()
        setupRecyclerView()
        setupAddTaskButton()
        setupLoginButton()
        updateUIBasedOnAuthState() // Set initial state of the login button
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Optional: if you don't want a title
    }

    private fun setupLoginButton() {
        // Assuming R.id.buttonLogin is the ID of the Button in your Toolbar
        // If using ViewBinding directly for the button in the toolbar, you might use binding.buttonLogin
        loginButton = findViewById<Button>(R.id.buttonLogin) 
        // The click listener will be set by updateUIBasedOnAuthState()
    }

    private fun updateUIBasedOnAuthState() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is signed in
            taskViewModel.refreshTasksFromServer()
            loginButton.text = "Logout"
            loginButton.setOnClickListener {
                signOut()
            }
        } else {
            // User is signed out
            loginButton.text = "Login"
            loginButton.setOnClickListener {
                startSignInFlow()
            }
        }
    }

    private fun startSignInFlow() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.ic_launcher_foreground) // optional
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                Toast.makeText(this, "You have been signed out.", Toast.LENGTH_SHORT).show()
                updateUIBasedOnAuthState()
            }
    }

    private fun setupViewModel() {
        val viewModelFactory = TaskViewModelFactory(application)
        taskViewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        taskViewModel.allTasks.observe(this) { tasks ->
            tasks?.let { taskAdapter.submitList(it) }
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskClicked = { task ->
                // Task clicked - toggle status and update
                val updatedTask = task.copy(isCompleted = !task.isCompleted)
                taskViewModel.update(updatedTask)
            },
            onDeleteClicked = { task ->
                // Delete task
                taskViewModel.delete(task)
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
            }
        )
        binding.recyclerViewTasks.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupAddTaskButton() {
        binding.buttonAddTask.setOnClickListener {
            val taskTitle = binding.editTextNewTask.text.toString().trim()
            if (taskTitle.isNotEmpty()) {
                taskViewModel.insert(Task(title = taskTitle))
                binding.editTextNewTask.text.clear()
            } else {
                Toast.makeText(this, "Task title cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            Toast.makeText(this, "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
            updateUIBasedOnAuthState()
            taskViewModel.refreshTasksFromServer() // <--- ADD THIS
        } else {
            // Sign in failed
            val response = result.idpResponse
            if (response == null) {
                Toast.makeText(this, "Sign-In Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Sign-In Failed: ${response.error?.message}", Toast.LENGTH_SHORT).show()
            }
        }
        updateUIBasedOnAuthState() // Update button text/action after sign-in attempt
    }

    // This function was unused and has been kept as is.
    private fun firebaseAuthWithGoogle(idToken: String) {}
}
