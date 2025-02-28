package com.example.farmbridge.data

import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.example.farmbridge.models.User
import com.example.farmbridge.navigation.ROUTE_HOME
import com.example.farmbridge.navigation.ROUTE_LOGIN
import com.example.farmbridge.navigation.ROUTE_REGISTER
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthViewModel(var navController: NavController, var context: Context){

    var mAuth: FirebaseAuth
    val progress: ProgressDialog

    init {
        mAuth= FirebaseAuth.getInstance()
        progress= ProgressDialog(context)
        progress.setTitle("Loading")
        progress.setMessage("PLease Wait.....")
    }
    fun signup(
        firstname:String,
        lastname:String,
        email:String,
       pass:String,
       confirmpass:String
    ){
        // progress.show()

        if (firstname.isBlank()|| lastname.isBlank()|| email.isBlank() || pass.isBlank() ||confirmpass.isBlank()){
            //progress.dismiss()
            Toast.makeText(context,"Please email and password cannot be blank", Toast.LENGTH_LONG).show()
            return
        }else if (pass != confirmpass){
            Toast.makeText(context,"Password do not match", Toast.LENGTH_LONG).show()
            return
        }else{
            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                if (it.isSuccessful){
                    val userdata= User(firstname, lastname, email,pass,mAuth.currentUser!!.uid)
                    val regRef= FirebaseDatabase.getInstance().getReference()
                        .child("Users/"+mAuth.currentUser!!.uid)
                    regRef.setValue(userdata).addOnCompleteListener {

                        if (it.isSuccessful){
                            Toast.makeText(context,"Registered Successfully", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUTE_HOME)

                        }else{
                            Toast.makeText(context,"${it.exception!!.message}", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUTE_LOGIN)
                        }
                    }
                }else{
                    navController.navigate(ROUTE_REGISTER)
                }

            } }

    }
    fun login(
        email: String,
        pass: String
    ){
        progress.show()
        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
            progress.dismiss()
            if (it.isSuccessful){
                Toast.makeText(context,"Successfully Logged in", Toast.LENGTH_LONG).show()
                navController.navigate(ROUTE_HOME)
//                navController.navigate(ROUTE_REGISTER)TO TAKE YOU TO A DIIFFERNT PAGE
            }else{
                Toast.makeText(context,"${it.exception!!.message}", Toast.LENGTH_LONG).show()
                navController.navigate(ROUTE_LOGIN)
            }
        }

    }
    fun logout(){
        mAuth.signOut()
        navController.navigate(ROUTE_LOGIN)
    }
    fun isloggedin():Boolean{
        return mAuth.currentUser !=null
    }

}