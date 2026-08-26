package com.example.mentor

import com.example.data.SportTracData
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MentorKnowledgeTest {

    @Test
    fun briefingNamesTheSportTracAndSelectedPart() {
        val component = SportTracData.components.first()
        val brief = MentorKnowledge.briefing(component)
        assertTrue(brief.vehicleLine.contains("2004"))
        assertTrue(brief.vehicleLine.contains("Sport Trac"))
        assertTrue(brief.componentName.isNotBlank())
        assertTrue(brief.oemPartNumber.isNotBlank())
        assertTrue(brief.uncertaintyNote.contains("workshop manual"))
    }

    @Test
    fun torqueQuestionUsesPackagedSpecsWhenPresent() {
        val component = SportTracData.components.first { it.torqueSpecs.isNotEmpty() }
        val answer = MentorKnowledge.answer(component, "what is the torque spec")
        assertTrue(answer.contains(component.name))
        assertFalse(answer.contains("inventing"))
    }

    @Test
    fun videoQuestionReturnsYoutubePointerAndDisclaimer() {
        val component = SportTracData.components.first()
        val answer = MentorKnowledge.answer(component, "show me a brake video")
        assertTrue(answer.contains("youtube.com/watch"))
        assertTrue(answer.contains("Community video only"))
    }
}
