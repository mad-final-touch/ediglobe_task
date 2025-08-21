package com.example.ediglobe_task

import android.app.Activity.RESULT_OK
import android.os.Bundle
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

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Instantiate a Google sign-in request
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



        val viewModelFactory = TaskViewModelFactory(application)
        taskViewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]

        setupRecyclerView()

        taskViewModel.allTasks.observe(this) { tasks ->
            tasks?.let { taskAdapter.submitList(it) }
        }

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


    private fun firebaseAuthWithGoogle(idToken: String) {}

private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
    val response = result.idpResponse
    if (result.resultCode == RESULT_OK) {
        // Successfully signed in
        val user = FirebaseAuth.getInstance().currentUser
        Toast.makeText(this, "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
    } else {
        // Sign in failed
        Toast.makeText(this, "Sign-In Failed", Toast.LENGTH_SHORT).show()
    }
}
}
