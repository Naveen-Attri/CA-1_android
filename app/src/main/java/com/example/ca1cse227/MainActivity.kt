package com.example.ca1cse227

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var etBookId: EditText
    private lateinit var etBookName: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnGetBook: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        database = FirebaseDatabase.getInstance().reference

        etBookId = findViewById(R.id.etBookId)
        etBookName = findViewById(R.id.etBookName)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnGetBook = findViewById(R.id.btnGetBook)

        btnSubmit.setOnClickListener {
            saveBook()
        }

        btnGetBook.setOnClickListener {
            getBook()
        }
    }

    private fun saveBook() {
        val bookId = etBookId.text.toString().trim()
        val bookName = etBookName.text.toString().trim()

        if (bookId.isEmpty() || bookName.isEmpty()) {
            Toast.makeText(this, "Please enter both book ID and name", Toast.LENGTH_SHORT).show()
        }


        val book = Book(bookId, bookName)

        database.child("books").child(bookId).setValue(book)
            .addOnSuccessListener {
                Toast.makeText(this, "Book saved successfully!", Toast.LENGTH_SHORT).show()
                etBookId.text.clear()
                etBookName.text.clear()
            }
    }

    private fun getBook() {
        val bookId = etBookId.text.toString().trim()

        if (bookId.isEmpty()) {
            Toast.makeText(this, "Please enter a book ID", Toast.LENGTH_SHORT).show()
        }

        database.child("books").child(bookId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val book = snapshot.getValue(Book::class.java)
                    if (book != null) {
                        etBookName.setText(book.name)
                        Toast.makeText(this@MainActivity, "Book retrieved successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Error retrieving book data", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Book not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to retrieve book: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}