package com.letthemcook.editor.domain.serialization

import com.letthemcook.editor.domain.editor.components.EmptyComponent
import com.letthemcook.editor.domain.editor.components.prototype.Component
import kotlinx.serialization.json.Json

object RecipeGraphSerializer {

    val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        explicitNulls = true
        prettyPrint = true
    }

    fun serializeToJson(component: Component): String {
        return ""
    }

    fun deserializeComponent(jsonString: String): Component {
        return EmptyComponent
    }
}