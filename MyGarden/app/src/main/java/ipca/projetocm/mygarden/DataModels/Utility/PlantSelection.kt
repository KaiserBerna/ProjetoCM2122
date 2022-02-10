package ipca.projetocm.mygarden.DataModels.Utility

import ipca.projetocm.mygarden.DataModels.*
import ipca.projetocm.mygarden.DataModels.PlantModels.Plants
import ipca.projetocm.mygarden.DataModels.PlantModels.Evergreen
import ipca.projetocm.mygarden.DataModels.PlantModels.OrnamentalTree
import ipca.projetocm.mygarden.DataModels.PlantModels.Rose
import ipca.projetocm.mygarden.DataModels.PlantModels.Hydrangea

class PlantSelection {

    // List to show which plants are available
    var allDifferentPlantTypes: MutableList<Plants> = ArrayList()

    // Counters for each type of plant selected
    var numberEvergreenSelected = 0
    var numberOrnamentalTreeSelected = 0
    var numberRoseSelected = 0
    var numberHydrangeaSelected = 0

    constructor(){

        predefinedListCreation()
    }

    constructor(slotReceivedForEdit : Slot){

        predefinedListCreation()
        countersDefinedByPreviousSlot(slotReceivedForEdit)
    }

    private fun countersDefinedByPreviousSlot(slotReceivedForEdit : Slot){

        numberEvergreenSelected = slotReceivedForEdit.evergreenCounter
        numberRoseSelected = slotReceivedForEdit.roseCounter
        numberOrnamentalTreeSelected = slotReceivedForEdit.ornamentalTreeCounter
        numberHydrangeaSelected = slotReceivedForEdit.hydrangeaCounter
    }

    // Creates List with all different type of plants
    private fun predefinedListCreation(){

        allDifferentPlantTypes.add(Evergreen())
        allDifferentPlantTypes.add((Rose()))
        allDifferentPlantTypes.add((OrnamentalTree()))
        allDifferentPlantTypes.add((Hydrangea()))
    }


}