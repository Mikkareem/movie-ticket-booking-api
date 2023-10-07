package dev.techullurgy.movieticketbooking.data.daos

import dev.techullurgy.movieticketbooking.data.schema.CustomerTable
import dev.techullurgy.movieticketbooking.domain.models.Customer
import dev.techullurgy.movieticketbooking.plugins.dbQuery
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

interface CustomerDao {
    suspend fun addCustomer(customer: Customer): Boolean

    suspend fun getCustomerById(id: Long): Customer?
}

class CustomerDaoImpl: CustomerDao {
    override suspend fun addCustomer(customer: Customer): Boolean {
        if(customer.id == -1L) return false
        dbQuery {
            CustomerTable.insert {
                it[name] = customer.name
            }
        }
        return true
    }

    override suspend fun getCustomerById(id: Long): Customer? {
        return dbQuery {
            CustomerTable.select { CustomerTable.id eq id }.map {
                Customer(id = it[CustomerTable.id], name = it[CustomerTable.name])
            }.firstOrNull()
        }
    }
}