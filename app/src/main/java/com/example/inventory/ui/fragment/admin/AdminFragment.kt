package com.example.inventory.ui.fragment.admin

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.inventory.databinding.FragmentAdminBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val db = Firebase.firestore

        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.tvAdmin
        textView.text = "Admin"

        val docRef1 = db.collection("Inventaris").document("DUPA0001")
        docRef1.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}