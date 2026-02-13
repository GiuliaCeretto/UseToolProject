package com.example.usetool.data.service

import com.example.usetool.data.dto.*
import com.example.usetool.data.network.FirebaseProvider

class UseToolService(private val dao: FirebaseProvider) {

    /**
     * Esegue aggiornamenti multipli in modo atomico.
     * La mappa contiene coppie "percorso/chiave" -> valore.
     */
    fun updateMultipleNodes(updates: Map<String, Any?>) {
        dao.getSlotsRef().parent?.updateChildren(updates)
    }

    fun updateSlotStatus(slotId: String, newStatus: String) {
        dao.getSlotsRef().child(slotId).child("status").setValue(newStatus)
    }

    fun savePurchase(purchase: PurchaseDTO) {
        val key = dao.getPurchasesRef().child(purchase.userId!!).push().key
        if (key != null) {
            dao.getPurchasesRef().child(purchase.userId).child(key)
                .setValue(purchase.copy(id = key))
        }
    }

    fun saveRental(rental: RentalDTO) {
        val key = dao.getRentalsRef().child(rental.userId!!).push().key
        if (key != null) {
            dao.getRentalsRef().child(rental.userId).child(key).setValue(rental.copy(id = key))
        }
    }

    fun updateCart(userId: String, cart: CartDTO) {
        dao.getCartsRef().child(userId).setValue(cart)
    }

    fun updateRentalRecord(userId: String, rental: RentalDTO) {
        if (rental.id != null) {
            dao.getRentalsRef().child(userId).child(rental.id).setValue(rental)
        }
    }
}