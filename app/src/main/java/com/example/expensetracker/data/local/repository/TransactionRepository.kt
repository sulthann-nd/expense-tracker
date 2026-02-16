package com.example.expensetracker.data.local.repository

import com.example.expensetracker.data.local.dao.TransactionDao
import com.example.expensetracker.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class TransactionRepository (
    // Change 'Any' to 'TransactionDao'
    private val transactionDao: TransactionDao
) {
    // Call the method on the instance (lowercase 't'), not the Interface
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    suspend fun insert(transaction: TransactionEntity) = transactionDao.insert(transaction)

    suspend fun update(transaction: TransactionEntity) = transactionDao.update(transaction)

    suspend fun delete(transaction: TransactionEntity) = transactionDao.delete(transaction)

    suspend fun getById(id: UUID) = transactionDao.getTransactionById(id)

    suspend fun getTransactionsByCategory(category: String): List<TransactionEntity> = transactionDao.getTransactionsByCategory(category)
}