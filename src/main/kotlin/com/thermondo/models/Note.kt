package com.thermondo.models

import java.time.Instant
import java.util.*
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Notes : UUIDTable() {
    var title = varchar("title", 255)
    val body = varchar("body", 255)
    val author = reference("author", Users)
    val isPublic = bool("isPublic")
    val createdAt = timestamp("createdAt").default(Instant.now())
    val updatedAt = timestamp("updatedAt").default(Instant.now())
}

object Tags : UUIDTable() {
    val tagName = varchar("tagName", 255).uniqueIndex()
}

object NoteTags : Table() {
    val note = reference(
        "note",
        Notes,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )
    val tag = reference(
        "tag",
        Tags,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    )

    override val primaryKey = PrimaryKey(note, tag)
}

class Tag(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Tag>(Tags)

    var tag by Tags.tagName
}

class Note(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Note>(Notes)

    var title by Notes.title
    var body by Notes.body
    var tags by Tag via NoteTags
    var author by Notes.author
    var isPublic by Notes.isPublic
    var createdAt by Notes.createdAt
    var updatedAt by Notes.updatedAt
}

data class NewNote(val note: Note) {
    data class Note(
        val title: String,
        val body: String,
        val isPublic: Boolean,
        val tagList: List<String> = emptyList()
    )
}

data class UpdateNote(val note: Note) {
    data class Note(
        val title: String? = null,
        val body: String? = null,
        val isPublic: Boolean? = null
    )
}

data class NoteResponse(val note: Note) {
    data class Note(
        val title: String,
        val body: String,
        val author: String,
        val tagList: List<String>
    )
}

data class MultipleNotesResponse(val notes: List<NoteResponse.Note>, val notesCount: Int)

data class TagResponse(val tags: List<String>)
