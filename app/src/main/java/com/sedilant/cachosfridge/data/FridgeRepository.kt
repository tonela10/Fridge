package com.sedilant.cachosfridge.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed interface AddFundsResult {
    data class Success(val personName: String) : AddFundsResult
    data object CardNotLinked : AddFundsResult
}

interface FridgeRepository {
    fun observeProducts(): Flow<List<ProductEntity>>
    fun observePeople(): Flow<List<PersonEntity>>
    fun observeBoteCents(): Flow<Int>
    suspend fun getProduct(productId: String): ProductEntity?
    suspend fun getPerson(personId: String): PersonEntity?
    suspend fun getPersonByNfcId(nfcId: String): PersonEntity?
    suspend fun ensureSeedData()
    suspend fun purchase(productId: String, personId: String, paymentMethod: PaymentMethod): PurchaseResult
    suspend fun purchaseWithCard(productId: String, nfcCardId: String): PurchaseResult
    suspend fun addFunds(personId: String, amountCents: Int)
    suspend fun addFundsByNfcCard(nfcCardId: String, amountCents: Int): AddFundsResult
    suspend fun addBote(amountCents: Int)
    suspend fun updateStock(productId: String, newStock: Int)
    suspend fun addProduct(product: ProductEntity)
    suspend fun updateProductPrice(productId: String, newPriceCents: Int)
    suspend fun deleteProduct(productId: String, hasAsset: Boolean): Boolean
    suspend fun settleDebt(personId: String)
    suspend fun createPerson(name: String, balanceCents: Int): PersonEntity
    suspend fun updatePersonDetails(personId: String, name: String, balanceCents: Int)
    suspend fun deletePerson(personId: String)
    suspend fun linkNfcCard(personId: String, nfcCardId: String)
    suspend fun unlinkNfcCard(personId: String)
}

class FridgeRepositoryImpl(
    private val db: FridgeDatabase,
    private val productDao: ProductDao,
    private val personDao: PersonDao,
    private val boteDao: BoteDao
) : FridgeRepository {

    override fun observeProducts(): Flow<List<ProductEntity>> = productDao.observeProducts()

    override fun observePeople(): Flow<List<PersonEntity>> = personDao.observePeople()

    override fun observeBoteCents(): Flow<Int> = boteDao.observeBote().map { it?.balanceCents ?: 0 }

    override suspend fun getProduct(productId: String): ProductEntity? = productDao.getProduct(productId)

    override suspend fun getPerson(personId: String): PersonEntity? = personDao.getPerson(personId)

    override suspend fun getPersonByNfcId(nfcId: String): PersonEntity? = personDao.getPersonByNfcId(nfcId)

    override suspend fun ensureSeedData() {
        if (productDao.countProducts() > 0) return
        db.withTransaction {
            productDao.insertAll(SeedData.products)
            personDao.insertAll(SeedData.people)
            boteDao.upsertBote(SeedData.bote)
        }
    }

    override suspend fun purchase(
        productId: String,
        personId: String,
        paymentMethod: PaymentMethod
    ): PurchaseResult {
        val product = productDao.getProduct(productId) ?: return PurchaseResult.NotFound
        val bote = boteDao.getBote() ?: BoteEntity(balanceCents = 0)

        if (product.stock <= 0) return PurchaseResult.ProductWithoutStock

        if (paymentMethod == PaymentMethod.PAY_WITH_BOTE && bote.balanceCents < product.priceCents) {
            return PurchaseResult.BoteInsufficient
        }

        db.withTransaction {
            productDao.updateProduct(product.copy(stock = product.stock - 1))
            when (paymentMethod) {
                PaymentMethod.PAY_NOW -> Unit
                PaymentMethod.PAY_WITH_CARD -> {
                    val person = personDao.getPerson(personId) ?: return@withTransaction
                    personDao.updatePerson(person.copy(balanceCents = person.balanceCents - product.priceCents))
                }
                PaymentMethod.PAY_WITH_BOTE -> {
                    boteDao.upsertBote(bote.copy(balanceCents = bote.balanceCents - product.priceCents))
                }
            }
        }

        return PurchaseResult.Success
    }

    override suspend fun purchaseWithCard(productId: String, nfcCardId: String): PurchaseResult {
        val person = personDao.getPersonByNfcId(nfcCardId) ?: return PurchaseResult.CardNotLinked
        return purchase(productId, person.id, PaymentMethod.PAY_WITH_CARD)
    }

    override suspend fun addFunds(personId: String, amountCents: Int) {
        if (amountCents <= 0) return
        val person = personDao.getPerson(personId) ?: return
        personDao.updatePerson(person.copy(balanceCents = person.balanceCents + amountCents))
    }

    override suspend fun addFundsByNfcCard(nfcCardId: String, amountCents: Int): AddFundsResult {
        if (amountCents <= 0) return AddFundsResult.CardNotLinked
        val person = personDao.getPersonByNfcId(nfcCardId) ?: return AddFundsResult.CardNotLinked
        personDao.updatePerson(person.copy(balanceCents = person.balanceCents + amountCents))
        return AddFundsResult.Success(person.name)
    }

    override suspend fun addBote(amountCents: Int) {
        if (amountCents <= 0) return
        val bote = boteDao.getBote() ?: BoteEntity(balanceCents = 0)
        boteDao.upsertBote(bote.copy(balanceCents = bote.balanceCents + amountCents))
    }

    override suspend fun updateStock(productId: String, newStock: Int) {
        val product = productDao.getProduct(productId) ?: return
        productDao.updateProduct(product.copy(stock = newStock.coerceAtLeast(0)))
    }

    override suspend fun addProduct(product: ProductEntity) {
        productDao.insertProduct(product)
    }

    override suspend fun updateProductPrice(productId: String, newPriceCents: Int) {
        val product = productDao.getProduct(productId) ?: return
        productDao.updateProduct(product.copy(priceCents = newPriceCents))
    }

    override suspend fun deleteProduct(productId: String, hasAsset: Boolean): Boolean {
        if (hasAsset) return false
        val product = productDao.getProduct(productId) ?: return false
        productDao.deleteProduct(product)
        return true
    }

    override suspend fun settleDebt(personId: String) {
        val person = personDao.getPerson(personId) ?: return
        if (person.balanceCents < 0) {
            personDao.updatePerson(person.copy(balanceCents = 0))
        }
    }

    override suspend fun createPerson(name: String, balanceCents: Int): PersonEntity {
        val id = name.lowercase().replace(" ", "_") + "_" + System.currentTimeMillis()
        val person = PersonEntity(id = id, name = name, balanceCents = balanceCents)
        personDao.insertPerson(person)
        return person
    }

    override suspend fun updatePersonDetails(personId: String, name: String, balanceCents: Int) {
        val person = personDao.getPerson(personId) ?: return
        personDao.updatePerson(person.copy(name = name, balanceCents = balanceCents))
    }

    override suspend fun deletePerson(personId: String) {
        val person = personDao.getPerson(personId) ?: return
        personDao.deletePerson(person)
    }

    override suspend fun linkNfcCard(personId: String, nfcCardId: String) {
        val person = personDao.getPerson(personId) ?: return
        personDao.updatePerson(person.copy(nfcCardId = nfcCardId))
    }

    override suspend fun unlinkNfcCard(personId: String) {
        val person = personDao.getPerson(personId) ?: return
        personDao.updatePerson(person.copy(nfcCardId = null))
    }
}
