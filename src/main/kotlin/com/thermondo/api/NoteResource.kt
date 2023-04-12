package com.thermondo.api

import com.thermondo.models.MultipleNotesResponse
import com.thermondo.models.NewNote
import com.thermondo.models.NoteResponse
import com.thermondo.models.TagResponse
import com.thermondo.models.UpdateNote
import com.thermondo.service.NoteService
import com.thermondo.util.param
import com.thermondo.util.userId
import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.put
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

fun Route.note(noteService: NoteService) {

    authenticate {

        post("/notes", {
            description = "Create Note"
            securitySchemeNames = setOf("SecurityScheme")
            request {
                body<NewNote>()
            }
            response {
                HttpStatusCode.OK to {
                    body<NoteResponse>()
                }
            }
        }) {
            val newNote = call.receive<NewNote>()
            val note = noteService.createNote(call.userId(), newNote)
            call.respond(note)
        }

        put("/notes/{id}", {
            description = "Update Note"
            securitySchemeNames = setOf("SecurityScheme")
            request {
                pathParameter<String>("id")
                body<UpdateNote>()
            }
            response {
                HttpStatusCode.OK to {
                    body<NoteResponse>()
                }
            }
        }) {
            val id = call.param("id")
            val updateNote = call.receive<UpdateNote>()
            val note = noteService.updateNote(call.userId(), id, updateNote)
            call.respond(note)
        }

        delete("/notes/{id}", {
            description = "Delete Note"
            securitySchemeNames = setOf("SecurityScheme")
            request {
                pathParameter<String>("id")
            }
            response {
                HttpStatusCode.OK
            }
        }) {
            val id = call.param("id")
            noteService.deleteNote(call.userId(), id)
            call.respond(HttpStatusCode.OK)
        }
    }

    authenticate(optional = true) {

        get("/notes", {
            description = "List Notes"
            request {
                queryParameter<String>("tag") {
                    description = "tag to filter by"
                    required = false
                }
            }
            response {
                HttpStatusCode.OK to {
                    body<List<NoteResponse.Note>>()
                }
            }
        }) {
            val userId = call.principal<UserIdPrincipal>()?.name
            val params = call.parameters
            val filter = mapOf(
                "tag" to params["tag"],
                "author" to params["author"],
                "limit" to params["limit"],
                "offset" to params["offset"]
            )
            val notes = noteService.getNotes(userId, filter)
            call.respond(MultipleNotesResponse(notes, notes.size))
        }
    }

    get("/notes/{id}", {
        description = "Get Note"
        request {
            pathParameter<String>("id")
        }
        response {
            HttpStatusCode.OK to {
                body<List<NoteResponse>>()
            }
        }
    }) {
        val id = call.param("id")
        val note = noteService.getNote(id)
        call.respond(note)
    }

    get("/tags", {
        description = "Get Tags"
        response {
            HttpStatusCode.OK to {
                body<TagResponse>()
            }
        }
    }) {
        call.respond(noteService.getAllTags())
    }
}
