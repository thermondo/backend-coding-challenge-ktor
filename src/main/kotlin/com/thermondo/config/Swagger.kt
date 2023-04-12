package com.thermondo.config

import io.github.smiley4.ktorswaggerui.SwaggerUIPluginConfig

fun SwaggerUIPluginConfig.swaggerUi() {

    swagger {
        swaggerUrl = "swagger-ui"
        forwardRoot = true
    }
    info {
        title = "API"
        version = "latest"
    }
    server {
        url = "http://localhost:8080"
        description = "Development Server"
    }
    defaultUnauthorizedResponse {
        description = "Username or password is invalid."
    }
    securityScheme("SecurityScheme") {
        type = io.github.smiley4.ktorswaggerui.dsl.AuthType.HTTP
        location = io.github.smiley4.ktorswaggerui.dsl.AuthKeyLocation.HEADER
        scheme = io.github.smiley4.ktorswaggerui.dsl.AuthScheme.BEARER
    }
    schemasInComponentSection = true
    examplesInComponentSection = true
    automaticTagGenerator = { url -> url.firstOrNull() }
}
