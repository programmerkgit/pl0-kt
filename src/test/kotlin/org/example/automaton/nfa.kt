package org.example.automaton

import org.junit.Test
import org.junit.Assert.assertEquals


class NFATest {
    @Test
    fun testNftMove() {
        val nft = NFA()
        nft.move("")
        assertEquals(nft.isFinalState(), true)
    }
}