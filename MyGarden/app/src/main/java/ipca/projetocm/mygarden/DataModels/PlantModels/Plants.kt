package ipca.projetocm.mygarden.DataModels.PlantModels

import java.io.Serializable

abstract class Plants : Serializable{

    var name: String? = null
    var image: Int? = null
    var hour: Int? = null
}