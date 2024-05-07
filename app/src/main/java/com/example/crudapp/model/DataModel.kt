package com.example.crudapp.model

import java.io.Serializable

class DataModel : Serializable {

    var nama: String? = null
    var nim: String? = null
    var prodi: String? = null
    var img: String? = ""

    // Add any other fields you need

    constructor(nama: String?, nim: String?, prodi: String?, img: String?) {
        this.nama = nama
        this.nim = nim
        this.prodi = prodi
        this.img = img

    }

    constructor() {

    }
}