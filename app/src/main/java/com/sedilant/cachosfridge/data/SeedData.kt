package com.sedilant.cachosfridge.data

object SeedData {
    val products = listOf(
        ProductEntity(id = "pepsi", name = "Pepsi", priceCents = 50, stock = 12, hasAsset = true),
        ProductEntity(id = "kas_naranja", name = "Kas Naranja", priceCents = 50, stock = 8, hasAsset = true),
        ProductEntity(id = "kas_limon", name = "Kas Limon", priceCents = 50, stock = 5, hasAsset = true),
        ProductEntity(id = "cerveza", name = "Cerveza", priceCents = 50, stock = 24, hasAsset = true),
        ProductEntity(id = "agua", name = "Agua", priceCents = 20, stock = 30, hasAsset = true),
        ProductEntity(id = "patatas", name = "Patatas", priceCents = 50, stock = 15, hasAsset = true),
        ProductEntity(id = "palomitas", name = "Palomitas", priceCents = 50, stock = 10, hasAsset = true)
    )

    val people = listOf(
        PersonEntity(id = "mario", name = "Mario", balanceCents = -100),
        PersonEntity(id = "raul", name = "Raul", balanceCents = 250),
        PersonEntity(id = "tono", name = "Toño", balanceCents = 575),
        PersonEntity(id = "paton", name = "Patón", balanceCents = -320),
        PersonEntity(id = "canut", name = "Canut", balanceCents = 840)
    )

    val bote = BoteEntity(balanceCents = 0)
}

