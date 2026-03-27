package com.dox.application.service

import com.dox.application.port.output.ReportPersistencePort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ReportBlockService(
    private val reportPersistencePort: ReportPersistencePort
) {

    fun extractSectionType(block: Map<String, Any?>): String {
        val data = block["data"] as? Map<*, *> ?: return "Seção"
        val title = data["title"]?.toString()
        val subtitle = data["subtitle"]?.toString()
        val label = data["label"]?.toString()
        return title?.takeIf { it.isNotBlank() }
            ?: subtitle?.takeIf { it.isNotBlank() }
            ?: label?.takeIf { it.isNotBlank() }
            ?: "Seção"
    }

    fun updateBlockContent(reportId: UUID, block: Map<String, Any?>, generatedText: String, skipped: Boolean = false) {
        val report = reportPersistencePort.findById(reportId) ?: return
        val blockId = block["id"]?.toString() ?: return

        val slateContent = textToSlateNodes(generatedText)

        val updatedBlocks = report.blocks.map { existingBlock ->
            if (existingBlock["id"]?.toString() == blockId) {
                val existingData = (existingBlock["data"] as? Map<*, *>)?.toMutableMap() ?: mutableMapOf()
                existingData["content"] = slateContent
                existingData["generatedByAi"] = true

                val mutableBlock = existingBlock.toMutableMap()
                mutableBlock["data"] = existingData
                if (skipped) mutableBlock["skippedByAi"] = true
                mutableBlock
            } else {
                existingBlock
            }
        }

        reportPersistencePort.save(report.copy(blocks = updatedBlocks))
    }

    fun insertContentBlockAfter(reportId: UUID, sectionBlock: Map<String, Any?>, generatedText: String, skipped: Boolean = false) {
        val report = reportPersistencePort.findById(reportId) ?: return
        val sectionBlockId = sectionBlock["id"]?.toString() ?: return

        val slateContent = textToSlateNodes(generatedText)
        val sectionIndex = report.blocks.indexOfFirst { it["id"]?.toString() == sectionBlockId }
        if (sectionIndex < 0) return

        val nextBlock = report.blocks.getOrNull(sectionIndex + 1)
        val nextData = nextBlock?.get("data") as? Map<*, *>
        val isExistingAiBlock = nextBlock != null
            && (nextBlock["generatedByAi"] == true || nextBlock["skippedByAi"] == true)
            && nextData?.get("title")?.toString().isNullOrBlank()

        if (isExistingAiBlock) {
            val updatedBlocks = report.blocks.mapIndexed { index, block ->
                if (index == sectionIndex + 1) {
                    val existingData = (block["data"] as? Map<*, *>)?.toMutableMap() ?: mutableMapOf()
                    existingData["content"] = slateContent

                    val mutableBlock = block.toMutableMap()
                    mutableBlock["data"] = existingData
                    mutableBlock["generatedByAi"] = true
                    if (skipped) mutableBlock["skippedByAi"] = true else mutableBlock.remove("skippedByAi")
                    mutableBlock
                } else {
                    block
                }
            }
            reportPersistencePort.save(report.copy(blocks = updatedBlocks))
        } else {
            val originalSectionBlock = report.blocks.find { it["id"]?.toString() == sectionBlockId }
            val blockFields = mutableMapOf<String, Any?>(
                "id" to UUID.randomUUID().toString(),
                "type" to "text",
                "parentId" to (originalSectionBlock?.get("id")?.toString()),
                "order" to 0,
                "collapsed" to false,
                "generatedByAi" to true,
                "data" to mapOf(
                    "content" to slateContent,
                    "labeledItems" to emptyList<Any>(),
                    "useLabeledItems" to false
                )
            )
            if (skipped) blockFields["skippedByAi"] = true

            val updatedBlocks = mutableListOf<Map<String, Any?>>()
            for (existingBlock in report.blocks) {
                updatedBlocks.add(existingBlock)
                if (existingBlock["id"]?.toString() == sectionBlockId) {
                    updatedBlocks.add(blockFields)
                }
            }

            var order = 0
            val reorderedBlocks = updatedBlocks.map { block ->
                val mutableBlock = block.toMutableMap()
                mutableBlock["order"] = order++
                mutableBlock
            }

            reportPersistencePort.save(report.copy(blocks = reorderedBlocks))
        }
    }

    private fun textToSlateNodes(text: String): List<Map<String, Any>> {
        val paragraphs = text.split("\n\n").filter { it.isNotBlank() }
        if (paragraphs.isEmpty()) {
            return listOf(mapOf(
                "id" to generateSlateId(),
                "type" to "p",
                "children" to listOf(mapOf("text" to ""))
            ))
        }
        return paragraphs.map { paragraph ->
            mapOf(
                "id" to generateSlateId(),
                "type" to "p",
                "children" to listOf(mapOf("text" to paragraph.trim()))
            )
        }
    }

    private fun generateSlateId(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-"
        return (1..10).map { chars.random() }.joinToString("")
    }
}
