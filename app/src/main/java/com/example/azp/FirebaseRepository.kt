package com.example.azp

import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

interface FirebaseRepositoryCallback<T> {
    fun onSuccess(result: List<Task>)
    fun onError(e: Exception)
}


class FirebaseRepository(private val rootNode: String, private val mapper: FirebaseMapper) :
    FirebaseRepositoryCallback<Task> {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference(rootNode)

    fun add(item: Task, callback: FirebaseRepositoryCallback<Task>) {
        val key = databaseReference.push().key ?: return
        databaseReference.child(key).setValue(mapper.toHashMap(item))
            .addOnSuccessListener {
                callback.onSuccess(listOf(item))
            }
            .addOnFailureListener {
                callback.onError(it)
            }
    }

    fun getAll(callback: FirebaseRepositoryCallback<Task>) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.map { mapper.fromHashMap(it.value as HashMap<*, *> ) }.toList()
                callback.onSuccess(items)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onError(error.toException())
            }
        })
    }

    fun get(id: String, callback: FirebaseRepositoryCallback<Task>) {
        databaseReference.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val item = mapper.fromHashMap(snapshot.value as HashMap<*, *> )
                    callback.onSuccess(listOf(item))
                } else {
                    callback.onSuccess(emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onError(error.toException())
            }
        })
    }

    fun update(item: Task, callback: FirebaseRepositoryCallback<Task>) {
        val id = item.getId() ?: return
        databaseReference.child(id).setValue(mapper.toHashMap(item))
            .addOnSuccessListener {
                callback.onSuccess(listOf(item))
            }
            .addOnFailureListener {
                callback.onError(it)
            }
    }

    fun delete(id: String, callback: FirebaseRepositoryCallback<Task>) {
        databaseReference.child(id).removeValue()
            .addOnSuccessListener {
                callback.onSuccess(emptyList()) // Assuming no data to return after deletion
            }
            .addOnFailureListener {
                callback.onError(it)
            }
    }

    override fun onSuccess(result: List<Task>) {

    }

    override fun onError(e: Exception) {

    }

}

interface Mapper<T> {
    fun toHashMap(model: T): HashMap<String, Any>
    fun fromHashMap(hashMap: HashMap<*, *>): T
}
class FirebaseMapper : Mapper<Task> {

    override fun toHashMap(model: Task): HashMap<String, Any> {
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = model.getId()
        hashMap["title"] = model.getTitle()
        hashMap["description"] = model.getDescription()
        hashMap["state"] = model.getState()
        hashMap["dueDate"] = model.getDueDate()

        return hashMap
    }

    override fun fromHashMap(hashMap: HashMap<*, *>): Task {
        val id = hashMap["id"] as String
        val title = hashMap["title"] as String
        val description = hashMap["description"] as String
        val state = hashMap["state"] as TaskState
        val dueDate = hashMap["dueDate"] as Long
        return Task(id, title, description, state, dueDate)
    }
}