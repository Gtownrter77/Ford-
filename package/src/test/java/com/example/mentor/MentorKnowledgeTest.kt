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

    @Test
    fun workshopQuestionReturnsFourWheelDriveCharmLeaf() {
        val component = SportTracData.components.first { it.name.contains("Transfer", ignoreCase = true) }
        val answer = MentorKnowledge.answer(component, "open the CHARM workshop page")
        assertTrue(answer.contains("charm.li"))
        assertTrue(answer.contains("4WD"))
        assertFalse(answer.contains("2WD"))
        val brief = MentorKnowledge.briefing(component)
        assertTrue(brief.workshopLeaves.isNotEmpty())
        brief.workshopLeaves.forEach { leaf ->
            assertFalse(leaf.contains("2WD"))
        }
    }

    @Test
    fun officialOwnerGuideQuestionReturnsFordHostedPdf() {
        val component = SportTracData.components.first()
        val answer = MentorKnowledge.answer(component, "open the official owner guide pdf")
        assertTrue(answer.contains("fordservicecontent"))
        assertTrue(answer.contains("04p27og"))
        assertTrue(answer.contains("Not the workshop manual"))
    }

    @Test
    fun forumQuestionReturnsExplorerForumDisclaimer() {
        val component = SportTracData.components.first()
        val answer = MentorKnowledge.answer(component, "explorerforum timing chain rattle thread")
        assertTrue(answer.contains("explorerforum"))
        assertTrue(answer.contains("Community thread only"))
    }

    @Test
    fun axleQuestionUsesSeventyFiveWNinetyAndXl7() {
        val component = SportTracData.components.first()
        val answer = MentorKnowledge.answer(component, "what rear axle fluid and XL-7")
        assertTrue(answer.contains("75W-90"))
        assertTrue(answer.contains("XL-7"))
        assertFalse(answer.contains("75W-140"))
    }
}
