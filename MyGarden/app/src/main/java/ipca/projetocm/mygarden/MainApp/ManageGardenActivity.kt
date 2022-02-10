package ipca.projetocm.mygarden.MainApp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ipca.projetocm.mygarden.AccountSettingsAndInfo.UserIdFirebase
import ipca.projetocm.mygarden.DataModels.*
import ipca.projetocm.mygarden.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.password_check.view.*
import java.util.ArrayList

class ManageGardenActivity : AppCompatActivity() {

    var slotList: MutableList<Slot> = ArrayList<Slot>()
    lateinit var ref: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()


        // Gets reference from correspondent node in Firebase of slot storage
        ref = FirebaseDatabase.getInstance().getReference(UserIdFirebase.UID!!)


        // Sets up adapter for the list
        slotListView.adapter = SlotAdapter()


        // Reads custom slots from Firebase
        readingFirebaseData()

        // Calls and manages result from EditSlotActivity
        slotManager()


    }

    // Reads the data from the associated Firebase and stores them in the list
    private fun readingFirebaseData() {


        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
              TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {

                    for (h in p0.children) {

                        // Bool to check if the node isn't already stored in the list
                        var alreadyInList: Boolean = false

                        // Gets current node slot
                        var slotInCurrentNode = h.getValue(Slot::class.java)


                        // Checks the list for a slot with the same id
                        for (s in slotList) {

                            // If it finds one it changes the bool variable to true
                            if (s.id != null && s.id == slotInCurrentNode!!.id) {

                                alreadyInList = true
                                break
                            }
                        }
                        // If the slot in the current node of the firebase isn't stored in the list it stores it
                        if (alreadyInList == false) {

                            slotList.add(slotInCurrentNode!!)
                        }
                    }
                    // Updates listView
                    slotListView.adapter = SlotAdapter()
                }
            }
        })
    }

    // Manages the button for the EditSlotActivity
    private fun slotManager() {

        addSlotButton.setOnClickListener {


            val intent = Intent(this, EditSlotActivity::class.java)
            startActivity(intent)

        }
    }


            // Manages activity results from EditSlotActivity
            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result from UpdateSlotActivity
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){

            // Gets which action was made (Update)
            var typeOfReturn : String = data?.getStringExtra("TypeOfReturn")!!

            // UPDATE ACTION
            if(typeOfReturn == "UPDATE"){

                // Gets data returned from the EditSlotActivity

                // Gets the id of the slot to update
                var slotIdToUpdate = data?.getStringExtra("SlotToUpdateId")

                // Gets the updated slot
                var slotUpdated =  data?.getSerializableExtra("SlotForUpdate") as Slot


                // Variable responsible for storing the index of the slot in the list to update
                var indexToSubstitute : Int = 0

                for(s in slotList){

                    if(s.id == slotIdToUpdate) indexToSubstitute = slotList.indexOf(s)

                }

                // Substitutes the slot with the same id
                slotList[indexToSubstitute] = slotUpdated

                // Small message pop up to show it went successfully
                Toast.makeText(this,"Slot Updated", Toast.LENGTH_LONG).show()

            }

            // DELETE ACTION
            else if(typeOfReturn == "DELETE"){

                // Gets the data of the id of the slot to remove
                var slotIdToRemove = data?.getStringExtra("SlotToRemoveId")

                // Searches the slot list for the slot with the same id to be removed
                for(s in slotList){

                    if(s.id == slotIdToRemove){

                        slotList.remove(s)
                        break
                    }
                }
                // Makes small message pop up
                Toast.makeText(this,"Slot Deleted", Toast.LENGTH_LONG).show()
            }
            // Updates listView
            slotListView.adapter = SlotAdapter()
        }
    }


            // Slot Adapter
            inner class SlotAdapter : BaseAdapter() {

                override fun getView(position: Int, convertView: View?, parente: ViewGroup?): View {

                    // Gets current slot
                    var currentSlot: Slot = getItem(position) as Slot

                    // gets view information
                    var v = layoutInflater.inflate(R.layout.plant_row, parente, false)

                    var slotNameView = v.findViewById<TextView>(R.id.slotNameView)
                    slotNameView.text = slotList[position].name

                    var slotPlantCountView = v.findViewById<TextView>(R.id.slotPlantCountView)
                    slotPlantCountView.text = slotList[position].numberOfPlants.toString()

                    var plantImageView = v.findViewById<ImageView>(R.id.plantImageView)
                    plantImageView.setImageResource(slotList[position].image!!)

                    var plantSprinkleView = v.findViewById<TextView>(R.id.plantSprinkleView)
                    plantSprinkleView.text =
                        "Sprinkle: " + currentSlot.sprinkleTime.toString() + "hours"

                    //sets up plant count info on screen
                    var plantNumbers =
                        "x" + currentSlot.evergreenCounter.toString() + " Evergreens \n" +
                                "x" + currentSlot.roseCounter.toString() + " Roses \n" +
                                "x" + currentSlot.ornamentalTreeCounter.toString() + " Ornamental Trees \n" +
                                "Total: " + currentSlot.numberOfPlants.toString()

                    slotPlantCountView.text = plantNumbers

                    // Manages item click
                    v.setOnClickListener {

                        var intent = Intent(this@ManageGardenActivity, UpdateSlotActivity::class.java)

                        intent.putExtra("CurrentSlot", getItem(position) as Slot)

                        startActivityForResult(intent,1)

            }

                    return v
                }

                override fun getItem(position: Int): Any {
                    return slotList[position]
                }

                override fun getItemId(position: Int): Long {
                    return 0
                }

                override fun getCount(): Int {
                    return slotList.size
                }

            }
    }



