package com.example.mentor

import com.example.data.SportTracData
import org.junit.Assert.assertTrue
import org.junit.Test

class OnModelInstructionTest {

    @Test
    fun everyCatalogComponentHasAtLeastOneOnModelInstruction() {
        SportTracData.components.forEach { component ->
            val instructions = OnModelInstructions.forComponent(component)
            assertTrue("${component.id} must expose an on-model instruction", instructions.isNotEmpty())
            assertTrue(instructions.all { it.nodeId == component.id })
            assertTrue(instructions.all { it.body.isNotBlank() })
        }
    }
}
