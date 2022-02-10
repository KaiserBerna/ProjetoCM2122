package ipca.projetocm.mygarden.MainApp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import ipca.projetocm.mygarden.AccountSettingsAndInfo.UserIdFirebase
import ipca.projetocm.mygarden.DataModels.*
import ipca.projetocm.mygarden.DataModels.PlantModels.Plants
import ipca.projetocm.mygarden.DataModels.PlantModels.Evergreen
import ipca.projetocm.mygarden.DataModels.PlantModels.Hydrangea
import ipca.projetocm.mygarden.DataModels.PlantModels.OrnamentalTree
import ipca.projetocm.mygarden.DataModels.PlantModels.Rose
import ipca.projetocm.mygarden.DataModels.Utility.PlantSelection
import ipca.projetocm.mygarden.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.update_slot_activity.*
import java.util.ArrayList

class UpdateSlotActivity : AppCompatActivity (){

    lateinit var plantSelectionManager : PlantSelection
    lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.update_slot_activity)

        supportActionBar!!.hide()

        // Gets slots selected
        var slotReceived = intent.getSerializableExtra("CurrentSlot") as Slot

        // Gets reference from correspondent node in firebase of slot storage
        ref = FirebaseDatabase.getInstance().getReference(UserIdFirebase.UID!!)

        // Creates a plant selection manager with the starting values as the ones of the slot
        plantSelectionManager =
            PlantSelection(slotReceived)

        // Gets the toolbar title to be the same as the selected slot
        getSupportActionBar()!!.setTitle(slotReceived.name)

        // Updates listView
        allPlantTypeForEditView.adapter = PlantTypeListUpdateAdapter()

        // Manages the updateButton click and substitutes value in Firebase
        updateButton.setOnClickListener{

            // Gets updated slot object and associates with the id of the one to be updated
            var slotUpdated = updateSlot()
            slotUpdated.id = slotReceived.id

            // Substitutes the slot in the Firebase
            ref.child(slotReceived.id!!).setValue(slotUpdated)

            // Creates intent to return necessary info
            var resultIntent = Intent()

            // Returns the info to know which action the user chose
            resultIntent.putExtra("TypeOfReturn", "UPDATE")

            // Returns the necessary info to update the slot in the ManageGardenActivity list of slots
            resultIntent.putExtra("SlotToUpdateId",slotUpdated.id )
            resultIntent.putExtra("SlotForUpdate", slotUpdated)

            setResult(Activity.RESULT_OK, resultIntent)

            finish()

        }

        deleteButton.setOnClickListener{


            // Removes the node from the Firebase of the selected slot
            ref.child(slotReceived.id!!).removeValue()


            // Intent made to return the id of the node removed so it can be removed from the list as well
            var resultIntent = Intent()

            resultIntent.putExtra("TypeOfReturn", "DELETE")
            resultIntent.putExtra("SlotToRemoveId",slotReceived.id )

            setResult(Activity.RESULT_OK, resultIntent)

            finish()

        }

    }

    private fun updateSlot() : Slot{



        //creates temporary plant list for custom slot creation
        var plantListForCustomSlot : MutableList<Plants> = ArrayList<Plants>()

        for(x in 1..plantSelectionManager.numberEvergreenSelected) plantListForCustomSlot.add(Evergreen())
        for(x in 1..plantSelectionManager.numberRoseSelected) plantListForCustomSlot.add(Rose())
        for(x in 1..plantSelectionManager.numberOrnamentalTreeSelected) plantListForCustomSlot.add(OrnamentalTree())
        for(x in 1..plantSelectionManager.numberHydrangeaSelected) plantListForCustomSlot.add(Hydrangea())

        return Slot("Custom Slot", plantListForCustomSlot, imageChoosing())
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

    inner class PlantTypeListUpdateAdapter : BaseAdapter() {


        override fun getView(position: Int, convertView: View?, parente: ViewGroup?): View {

            var currentPlant : Plants = getItem(position) as Plants

            // gets view information
            var v = layoutInflater.inflate(R.layout.plant_type_row, parente, false)

            var textViewName = v.findViewById<TextView>(R.id.plantTypeNameView)
            textViewName.text = currentPlant.name.toString()


            var flowerImageView = v.findViewById<ImageView>(R.id.plantTypeImageView)
            flowerImageView.setImageResource(currentPlant.image!!)



            // Gets adding and removing plant buttons
            var minusButtonView = v.findViewById(R.id.minusButton) as Button
            var plusButtonView = v.findViewById(R.id.plusButton) as Button


            // Gets current plant type number
            var currentPlantTypeSelectionView =  v.findViewById(R.id.plantTypeNumberSelection) as EditText



            when(currentPlant){

                is Evergreen -> currentPlantTypeSelectionView.text = Editable.Factory.getInstance().newEditable(plantSelectionManager.numberEvergreenSelected.toString())
                is Rose -> currentPlantTypeSelectionView.text = Editable.Factory.getInstance().newEditable(plantSelectionManager.numberRoseSelected.toString())
                is OrnamentalTree -> currentPlantTypeSelectionView.text = Editable.Factory.getInstance().newEditable(plantSelectionManager.numberOrnamentalTreeSelected.toString())
                is Hydrangea -> currentPlantTypeSelectionView.text = Editable.Factory.getInstance().newEditable(plantSelectionManager.numberHydrangeaSelected.toString())

            }

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



        private fun currentPlantTypeNumberStoring (currentNumber : Int, currentPlants : Plants) {


            when(currentPlants){

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
