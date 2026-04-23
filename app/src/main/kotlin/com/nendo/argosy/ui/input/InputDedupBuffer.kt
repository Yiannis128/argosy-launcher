package com.nendo.argosy.ui.input

/**
 * Fixed-size ring buffer of recently-seen [InputSignature]s. Used to drop duplicate
 * raw input events that reach more than one dispatch path (e.g. primary activity
 * vs. secondary/companion activity in dual-screen mode).
 *
 * Thread-safe: [claim] is synchronized on the buffer itself. The hot path performs
 * no allocations -- a small linear scan over a fixed array is cheaper than a hash
 * set at this size and avoids GC pressure in the input loop.
 */
class InputDedupBuffer(capacity: Int = DEFAULT_CAPACITY) {

    private val slots: Array<InputSignature?> = arrayOfNulls(capacity)
    private var writeIndex = 0

    /**
     * Records [signature] and returns `true` if this is the first time it has been
     * seen within the buffer window, `false` if it is already present (a duplicate).
     * The oldest signature rotates out when the buffer is full.
     */
    fun claim(signature: InputSignature): Boolean = synchronized(slots) {
        for (existing in slots) {
            if (existing == signature) return false
        }
        slots[writeIndex] = signature
        writeIndex = (writeIndex + 1) % slots.size
        true
    }

    companion object {
        private const val DEFAULT_CAPACITY = 30
    }
}
