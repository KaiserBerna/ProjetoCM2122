package ipca.projetocm.mygarden.DataModels


import android.widget.CheckBox
import ipca.projetocm.mygarden.DataModels.PlantModels.Evergreen
import ipca.projetocm.mygarden.DataModels.PlantModels.Hydrangea
import ipca.projetocm.mygarden.DataModels.PlantModels.Plants
import ipca.projetocm.mygarden.DataModels.PlantModels.OrnamentalTree
import ipca.projetocm.mygarden.DataModels.PlantModels.Rose
import java.io.Serializable
import java.util.ArrayList

class Slot : Serializable {

    var id :String? = null
    var name :String?= null
    private var plants : MutableList<Plants> = ArrayList<Plants>()
    var numberOfPlants : Int?= null
    var image : Int? = null

    var isChecked :Boolean? = true


    var evergreenCounter : Int = 0
    var hydrangeaCounter : Int = 0
    var roseCounter : Int = 0
    var ornamentalTreeCounter : Int = 0
    var sprinkleTime : Int = 0



    constructor(name : String, plantsList : MutableList<Plants>, image : Int)
    {
        this.name = name
        this.plants = plantsList
        this.numberOfPlants = plants.count()
        this.image = image

        getEachPlantTypeCount()
        sprinkleTime()
    }

    // Used for reading in firebase without returning Exception
    constructor()



    private fun getEachPlantTypeCount(){

        for(p in plants){

            if(p is Evergreen) evergreenCounter++
            else if (p is OrnamentalTree) ornamentalTreeCounter++
            else if (p is Rose) roseCounter++
            else if (p is Hydrangea) hydrangeaCounter++


        }
    }

    private fun sprinkleTime()
    {
        for(p in plants){

            if(p is Evergreen) sprinkleTime = p.hour!!
            else if (p is OrnamentalTree) sprinkleTime = p.hour!!
            else if (p is Rose) sprinkleTime = p.hour!!
            else if (p is Hydrangea) sprinkleTime = p.hour!!


        }
    }

    public fun UpdateCheck(checkBoxView: CheckBox) {


        this.isChecked = checkBoxView.isChecked
    }
}