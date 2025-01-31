package com.letthemcook.editor.domain.serialization

import android.util.Log
import com.letthemcook.core.data.files.FilesManager
import com.letthemcook.editor.domain.editor.components.EmptyComponent
import com.letthemcook.editor.domain.editor.components.block.BlockComponent
import com.letthemcook.editor.domain.editor.components.composed.ComposedComponent
import com.letthemcook.editor.domain.editor.components.composed.HorizontalComposedComponent
import com.letthemcook.editor.domain.editor.components.composed.VerticalComposedComponent
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.domain.editor.components.prototype.doForEveryChild
import com.letthemcook.editor.domain.editor.components.prototype.doForEveryChildAsync
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

class RecipeGraphSerializer(
    private val filesManager: FilesManager
) {

    private val jsonSerializationModule = SerializersModule {
        polymorphic(Component::class) {
            subclass(EmptyComponent::class)
            subclass(BlockComponent::class)
            subclass(HorizontalComposedComponent::class)
            subclass(VerticalComposedComponent::class)
        }

        polymorphic(ComposedComponent::class) {
            subclass(HorizontalComposedComponent::class)
            subclass(VerticalComposedComponent::class)
        }
    }

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        explicitNulls = true
        prettyPrint = true

        serializersModule = jsonSerializationModule
    }

    fun serializeToJson(component: Component): String {
        return json.encodeToString(component)
    }

    suspend fun deserializeComponent(jsonString: String): Component {
        return try {
            val components = listOf(json.decodeFromString<Component>(jsonString))

            components.doForEveryChildAsync {
                (this as? ComposedComponent)?.components?.forEach {
                    it.parentComponent = this
                }

                withContext(Dispatchers.IO) {
                    (this@doForEveryChildAsync as? BlockComponent)?.apply {
                        file = filesManager.getFileByName(id)
                    }
                }
            }

            components.first()
        } finally {
            EmptyComponent
        }
    }
}