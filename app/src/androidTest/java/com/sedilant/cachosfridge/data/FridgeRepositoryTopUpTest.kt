package com.sedilant.cachosfridge.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FridgeRepositoryTopUpTest {
    private lateinit var db: FridgeDatabase
    private lateinit var repository: FridgeRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, FridgeDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = FridgeRepositoryImpl(
            db = db,
            productDao = db.productDao(),
            personDao = db.personDao(),
            boteDao = db.boteDao(),
            transactionDao = db.transactionDao(),
            appSettingsDao = db.appSettingsDao(),
            topUpRequestDao = db.topUpRequestDao()
        )
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun approvalCreditsOnceAndAllowsOverpaymentToBecomePrepaidBalance() = runBlocking {
        insertPerson(balanceCents = -300)
        repository.updatePayPalPoolUrl("https://paypal.com/pool/cachos")

        val created = repository.createTopUpRequest(PERSON_ID, 500)
        assertTrue(created is TopUpRequestResult.Success)
        val requestId = (created as TopUpRequestResult.Success).request.id

        assertEquals(TopUpResolutionResult.Success, repository.approveTopUpRequest(requestId))
        assertEquals(TopUpResolutionResult.AlreadyResolved, repository.approveTopUpRequest(requestId))
        assertEquals(200, repository.getPerson(PERSON_ID)?.balanceCents)

        val transactions = repository.observeTransactions().first()
        assertEquals(1, transactions.size)
        assertEquals(TransactionType.ADD_FUNDS, transactions.single().type)
        assertEquals(500, transactions.single().amountCents)
    }

    @Test
    fun rejectionDoesNotChangeBalanceOrHistory() = runBlocking {
        insertPerson(balanceCents = -300)
        repository.updatePayPalPoolUrl("https://paypal.com/pool/cachos")
        val created = repository.createTopUpRequest(PERSON_ID, 300) as TopUpRequestResult.Success

        assertEquals(
            TopUpResolutionResult.Success,
            repository.rejectTopUpRequest(created.request.id)
        )
        assertEquals(-300, repository.getPerson(PERSON_ID)?.balanceCents)
        assertTrue(repository.observeTransactions().first().isEmpty())
    }

    @Test
    fun duplicatePendingRequestIsPrevented() = runBlocking {
        insertPerson(balanceCents = 0)
        repository.updatePayPalPoolUrl("https://paypal.com/pool/cachos")

        assertTrue(repository.createTopUpRequest(PERSON_ID, 500) is TopUpRequestResult.Success)
        assertTrue(
            repository.createTopUpRequest(PERSON_ID, 200) is TopUpRequestResult.AlreadyPending
        )
        assertEquals(1, repository.observePendingTopUpRequests().first().size)
    }

    @Test
    fun invalidMissingPersonAndMissingPoolAreRejected() = runBlocking {
        assertEquals(
            TopUpRequestResult.InvalidAmount,
            repository.createTopUpRequest(PERSON_ID, 0)
        )
        assertEquals(
            TopUpRequestResult.PoolNotConfigured,
            repository.createTopUpRequest(PERSON_ID, 100)
        )

        repository.updatePayPalPoolUrl("https://paypal.com/pool/cachos")
        assertEquals(
            TopUpRequestResult.PersonNotFound,
            repository.createTopUpRequest(PERSON_ID, 100)
        )
    }

    private suspend fun insertPerson(balanceCents: Int) {
        db.personDao().insertPerson(
            PersonEntity(
                id = PERSON_ID,
                name = "Mario",
                balanceCents = balanceCents,
                nfcCardId = "A1B2C3"
            )
        )
    }

    private companion object {
        const val PERSON_ID = "mario"
    }
}
