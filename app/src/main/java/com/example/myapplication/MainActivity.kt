package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    var firebaseAuth = FirebaseAuth.getInstance()
    var firestore = Firebase.firestore.collection("Persons")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            var Email = remember {
                mutableStateOf("")
            }
            var password = remember {
                mutableStateOf("")
            }

            var Name = remember {
                mutableStateOf("")
            }
            var age = remember {
                mutableStateOf("")
            }

            var Persons = remember {
                mutableStateOf("")
            }
            Column(verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxSize()) {
                Row(horizontalArrangement = Arrangement.SpaceBetween,modifier = Modifier.fillMaxWidth()) {
                    TextField(value = Email.value, onValueChange = { Email.value = it },label = { Text("Email") })
                    TextField(value = password.value, onValueChange = { password.value = it },label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())
                }
                Row(horizontalArrangement = Arrangement.SpaceEvenly,modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { this@MainActivity.LogInUser(Email.value,password.value)}) {
                        Text("Login")
                    }
                    Button(onClick = {  RegisterUser(Email.value,password.value) }) {
                        Text("Register")
                    }
                    Button(onClick = {  if(firebaseAuth.currentUser != null) { firebaseAuth.signOut();Toast.makeText(applicationContext,"Logged out SuccessFully",Toast.LENGTH_LONG).show() } }) {
                        Text("LogOut")
                    }
                }

                Row(horizontalArrangement = Arrangement.SpaceBetween,modifier = Modifier.fillMaxWidth()) {
                    TextField(value = Name.value, onValueChange = { Name.value = it },label = { Text("Name") })
                    TextField(
                        value = age.value,
                        onValueChange = { age.value = it },
                        label = { Text("Age") })
                }

                Row(horizontalArrangement = Arrangement.SpaceAround,modifier = Modifier.fillMaxWidth()){
                    Button(onClick = { InsertPerson(Name.value,age.value) }) {
                        Text("Insert")
                    }

                    Button(onClick = { GetPerson(Persons) }) {
                        Text("Load")
                    }
                }
                
                
                Text(text = Persons.value)

            }
        }
    }


    public fun RegisterUser(email:String,password:String){
        CoroutineScope(Dispatchers.IO).launch{
            try {
                firebaseAuth.createUserWithEmailAndPassword(email,password).await()
                withContext(Dispatchers.Main){
                    Toast.makeText(applicationContext,"Registered SuccessFully",Toast.LENGTH_LONG).show()
                }
            }
            catch (ex:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(applicationContext,ex.message,Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    public fun LogInUser(email:String,password:String){
        CoroutineScope(Dispatchers.IO).launch{
            try {
                firebaseAuth.signInWithEmailAndPassword(email,password).await()
                if (firebaseAuth.currentUser != null){
                    withContext(Dispatchers.Main){
                        Toast.makeText(applicationContext,"Logged In SuccessFully",Toast.LENGTH_LONG).show()
                    }
                }

            }
            catch (ex:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(applicationContext,ex.message,Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    public fun InsertPerson(name:String,age:String){
        CoroutineScope(Dispatchers.IO).launch{
            try {
                var hash = HashMap<String,String>()
                hash.set("Name",name)
                hash.set("Age",age)
                firestore.add(hash).await()

                withContext(Dispatchers.Main){
                    Toast.makeText(applicationContext,"Data Is Saved",Toast.LENGTH_LONG).show()
                }
            }
            catch (ex:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(applicationContext,ex.message,Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    public fun GetPerson(names:MutableState<String>){
        CoroutineScope(Dispatchers.IO).launch{
            try {
                var listNames = StringBuilder()

                for (data in firestore.get().await()){
                    listNames.append("Name: ${data.get("Name")} Age:${data.get("Age")}\n")
                }
                withContext(Dispatchers.Main){
                    names.value = listNames.toString()
                    Toast.makeText(applicationContext,"Data Is Loaded",Toast.LENGTH_LONG).show()
                }
            }
            catch (ex:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(applicationContext,ex.message,Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}



