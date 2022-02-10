package ipca.projetocm.mygarden.MainApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ipca.projetocm.mygarden.AccountSettingsAndInfo.UserIdFirebase
import ipca.projetocm.mygarden.DataModels.*
import ipca.projetocm.mygarden.DataModels.PlantModels.Plants
import ipca.projetocm.mygarden.DataModels.PlantModels.Evergreen
import ipca.projetocm.mygarden.DataModels.PlantModels.OrnamentalTree
import ipca.projetocm.mygarden.DataModels.PlantModels.Rose
import ipca.projetocm.mygarden.DataModels.PlantModels.Hydrangea
import ipca.projetocm.mygarden.DataModels.Utility.PlantSelection
import ipca.projetocm.mygarden.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.edit_slot_activity.*
import java.util.ArrayList

class EditSlotActivity : AppCompatActivity() {

    var plantSelectionManager : PlantSelection =
        PlantSelection()

    lateinit var ref : DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_slot_activity)
        supportActionBar!!.hide()

        // Gets reference from corresponding node in Firebase of slot storage
        ref = FirebaseDatabase.getInstance().getReference(UserIdFirebase.UID!!)

        // Sets up custom adapter
        allPlantTypeView.adapter = PlantTypeListAdapter()


        // Manages confirmButton action
        confirmButtonManagement()

    }


    // Manages Confirm Button action
    private fun confirmButtonManagement(){

        // Manages button click
        confirmAdd.setOnClickListener(){


            // Creates slot from selected plants
            var slot = createSlot()

            // If there were plants selected
            if(slot.numberOfPlants!! > 0){

                // Gets new id in the Firebase for the new created slot
                val slotId = ref.push().key
                slot.id = slotId

                // Adds the new slot to the Firebase
                ref.child(slotId!!).setValue(slot).addOnCompleteListener{

                    // Makes pop up message confirming the save
                    Toast.makeText(this,"Slot Saved!", Toast.LENGTH_LONG).show()

                }

            }
            else{
                // Makes pop up message telling there weren't plants selected so the slot was not saved
                Toast.makeText(this,"No Plants Selected\nSlot Not Saved!", Toast.LENGTH_LONG).show()
            }
            // Closes current activity and returns to main activity
            finish()
        }


    }



    // Creates custom slot from selected plants
    private fun createSlot() : Slot{

        // Creates temporary plant list for custom slot creation
        var plantListForSlot : MutableList<Plants> = ArrayList<Plants>()

        for(x in 1..plantSelectionManager.numberEvergreenSelected) plantListForSlot.add(Evergreen())
        for(x in 1..plantSelectionManager.numberRoseSelected) plantListForSlot.add(Rose())
        for(x in 1..plantSelectionManager.numberOrnamentalTreeSelected) plantListForSlot.add(OrnamentalTree())
        for(x in 1..plantSelectionManager.numberHydrangeaSelected) plantListForSlot.add(Hydrangea())

        // Gets custom name if the user enters one, otherwise gives it a default name ("Custom slot")
        var customName = when{

            customNameTextView.text.toString().isEmpty() -> "Slot"
            else -> customNameTextView.text.toString()
        }
        return  Slot(customName, plantListForSlot, imageChoosing())
    }

    private fun imageChoosing() : Int{

        var selectedImageforShow : Int


        if (plantSelectionManager.numberOrnamentalTreeSelected >= plantSelectionManager.numberRoseSelected)
        {
            if (plantSelectionManager.numberOrnamentalTreeSelected >= plantSelectionManager.numberEvergreenSelected)
            {
                if(plantSelectionManager.numberOrnamentalTreeSelected >= plantSelectionManager.numberHydrangeaSelected)
                {
                     selectedImageforShow = R.drawable.ornamental_tree_plant
                }
                else selectedImageforShow= R.drawable.hydrangea_plant
            }
            else selectedImageforShow = R.drawable.evergreen_plant
        }
        else
        {
            if (plantSelectionManager.numberRoseSelected >= plantSelectionManager.numberEvergreenSelected)
            {
                if (plantSelectionManager.numberRoseSelected >= plantSelectionManager.numberHydrangeaSelected)
                {
                    selectedImageforShow = R.drawable.rose_plant
                }
                else selectedImageforShow = R.drawable.hydrangea_plant
            }
            else selectedImageforShow = R.drawable.evergreen_plant
        }


        return selectedImageforShow
    }


    inner class PlantTypeListAdapter : BaseAdapter() {


        override fun getView(position: Int, convertView: View?, parente: ViewGroup?): View {

            var currentPlant : Plants = getItem(position) as Plants

            // gets view information
            var v = layoutInflater.inflate(R.layout.plant_type_row, parente, false)

            var textViewNome = v.findViewById<TextView>(R.id.plantTypeNameView)
            textViewNome.text = currentPlant.name.toString()


            var plantTypeImageView = v.findViewById<ImageView>(R.id.plantTypeImageView)
            plantTypeImageView.setImageResource( currentPlant.image!!)



            // Gets add and remove plants buttons
            var minusButtonView = v.findViewById<Button>(R.id.minusButton)
            var plusButtonView = v.findViewById<Button>(R.id.plusButton)


            // Gets current plant type number
            var currentPlantTypeSelectionView =  v.findViewById<EditText>(R.id.plantTypeNumberSelection)


            var currentNumber = currentPlantTypeSelectionView.text.toString().toInt()


            currentPlantTypeNumberStoring(currentNumber,currentPlant)


            minusButtonView.setOnClickListener {

                var currentNumber = currentPlantTypeSelectionView.text.toString().toInt()
                currentNumber--


                currentPlantTypeSelectionView.text = Editable.Factory.getInstance().newEditable(currentNumber.toString())

                currentPlantTypeNumberStoring(currentNumber,currentPlant)


            }


            plusButtonView.setOnClickListener {

                var currentNumber = currentPlantTypeSelectionView.text.toString().toInt()

                currentNumber++


                currentPlantTypeSelectionView.text =  Editable.Factory.getInstance().newEditable(currentNumber.toString())

                currentPlantTypeNumberStoring(currentNumber,currentPlant)



            }

            return  v
        }



        private fun currentPlantTypeNumberStoring (currentNumber : Int, currentPlant : Plants) {


            when(currentPlant){

                is Evergreen -> {

                    plantSelectionManager.numberEvergreenSelected = currentNumber

                }
                is Rose ->{

                    plantSelectionManager.numberRoseSelected = currentNumber
                }
                is OrnamentalTree ->{

                    plantSelectionManager.numberOrnamentalTreeSelected = currentNumber
                }
                is Hydrangea ->{
                    plantSelectionManager.numberHydrangeaSelected = currentNumber
                }


            }



        }




        override fun getItem(position: Int): Any {
            return plantSelectionManager.allDifferentPlantTypes[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return plantSelectionManager.allDifferentPlantTypes.size
        }



    }

}