package com.thermondo.service

import com.thermondo.models.NewNote
import com.thermondo.models.Note
import com.thermondo.models.NoteResponse
import com.thermondo.models.Notes
import com.thermondo.models.Tag
import com.thermondo.models.TagResponse
import com.thermondo.models.Tags
import com.thermondo.models.UpdateNote
import com.thermondo.models.User
import com.thermondo.util.AuthorizationException
import com.thermondo.util.NoteDoesNotExist
import java.time.Instant
import java.util.*
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.or

interface NoteService {
    suspend fun createNote(userId: String, newNote: NewNote): NoteResponse

    suspend fun updateNote(userId: String, slug: String, updateNote: UpdateNote): NoteResponse

    suspend fun getNote(slug: String): NoteResponse

    suspend fun getNotes(userId: String? = null, filter: Map<String, String?>): List<NoteResponse.Note>

    suspend fun deleteNote(userId: String, slug: String)

    suspend fun getAllTags(): TagResponse
}

class NoteServiceImpl(private val databaseFactory: DatabaseFactory) : NoteService {

    override suspend fun createNote(userId: String, newNote: NewNote): NoteResponse {
        return databaseFactory.dbQuery {
            val user = getUser(userId)
            val note = Note.new {
                title = newNote.note.title
                body = newNote.note.body
                author = user.id
                isPublic = newNote.note.isPublic
            }
            val tags = newNote.note.tagList.map { tag -> getOrCreateTag(tag) }
            note.tags = SizedCollection(tags)
            getNoteResponse(note)
        }
    }

    override suspend fun updateNote(userId: String, id: String, updateNote: UpdateNote): NoteResponse {
        return databaseFactory.dbQuery {
            val user = getUser(userId)
            val note = getNoteById(id)
            if (!isNoteAuthor(note, user)) throw AuthorizationException()
            if (updateNote.note.title != null) {
                note.title = updateNote.note.title
            }
            if (updateNote.note.body != null) {
                note.body = updateNote.note.body
            }
            if (updateNote.note.isPublic != null) {
                note.isPublic = updateNote.note.isPublic
            }
            note.updatedAt = Instant.now()
            getNoteResponse(note)
        }
    }

    override suspend fun getNote(id: String): NoteResponse {
        return databaseFactory.dbQuery {
            val note = getNoteById(id)
            getNoteResponse(note)
        }
    }

    override suspend fun getNotes(userId: String?, filter: Map<String, String?>): List<NoteResponse.Note> {
        return databaseFactory.dbQuery {
            val user = if (userId != null) getUser(userId) else null
            getAllNotes(
                currentUser = user,
                isPublicInclude = true,
                tag = filter["tag"],
                authorUserName = filter["author"],
                limit = filter["limit"]?.toInt() ?: 20,
                offset = filter["offset"]?.toInt() ?: 0
            )
        }
    }

    override suspend fun deleteNote(userId: String, id: String) {
        databaseFactory.dbQuery {
            val user = getUser(userId)
            val note = getNoteById(id)
            if (!isNoteAuthor(note, user)) throw AuthorizationException()
            note.delete()
        }
    }

    override suspend fun getAllTags(): TagResponse {
        return databaseFactory.dbQuery {
            val tags = Tag.all().map { it.tag }
            TagResponse(tags)
        }
    }

    private fun getAllNotes(
        currentUser: User? = null,
        isPublicInclude: Boolean = true,
        tag: String? = null,
        authorUserName: String? = null,
        limit: Int = 20,
        offset: Int = 0,
    ): List<NoteResponse.Note> {
        val author = if (authorUserName != null) getUserByUsername(authorUserName) else null
        val notes = Note.find {
            if (author != null) (Notes.author eq author.id).or(Notes.isPublic eq isPublicInclude) else Op.TRUE
        }.limit(limit, offset.toLong()).orderBy(Notes.createdAt to SortOrder.DESC)
        val filteredNotes = notes.filter { note ->
            if (tag != null) {
                note.tags.any { it.tag == tag }
            } else {
                true
            }
        }
        return filteredNotes.map {
            getNoteResponse(it).note
        }
    }
}

fun getNoteById(id: String) =
    Note.find { Notes.id eq UUID.fromString(id) }.firstOrNull() ?: throw NoteDoesNotExist(id)

fun getOrCreateTag(tagName: String) =
    Tag.find { Tags.tagName eq tagName }.firstOrNull() ?: Tag.new { this.tag = tagName }

fun getNoteResponse(note: Note): NoteResponse {
    val tagList = note.tags.map { it.tag }
    return NoteResponse(
        note = NoteResponse.Note(
            title = note.title,
            body = note.body,
            author = note.author.toString(),
            tagList = tagList
        )
    )
}

fun isNoteAuthor(note: Note, user: User) = note.author == user.id
