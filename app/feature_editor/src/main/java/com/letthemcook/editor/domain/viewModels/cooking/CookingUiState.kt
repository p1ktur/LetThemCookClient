package com.letthemcook.editor.domain.viewModels.cooking

import com.letthemcook.editor.domain.cooking.CookingState
import com.letthemcook.editor.domain.cooking.track.EmptyTrackData
import com.letthemcook.editor.domain.cooking.track.TrackData
import com.letthemcook.editor.domain.editor.components.EmptyComponent
import com.letthemcook.editor.domain.editor.components.EndComponent
import com.letthemcook.editor.domain.editor.components.StartComponent
import com.letthemcook.editor.domain.editor.components.block.BlockComponent
import com.letthemcook.editor.domain.editor.components.prototype.Component
import com.letthemcook.editor.domain.viewModels.canvas.CanvasUiState

data class CookingUiState(
    // Cooking
    val cookingState: CookingState = CookingState.NOT_STARTED,
    val totalCookingTime: Long = 0L,
    val cookingTimeLeft: Long = 0L,
    val trackData: TrackData = EmptyTrackData,
    val trackDataCounter: Int = 0,
    val cookingProgress: Float = 0f,
    // Components
    val selectedBlock: BlockComponent? = null,
    val startComponent: StartComponent = StartComponent(),
    val centralComponent: Component = EmptyComponent,
    val endComponent: EndComponent = EndComponent(),
    // Canvas data
    val canvasUiState: CanvasUiState = CanvasUiState(),
    val canvasCounter: Int = 0
)

val testCookData = "{\n" +
        "  \"type\": \"horizontal_composed\",\n" +
        "  \"components\": [\n" +
        "    {\n" +
        "      \"type\": \"vertical_composed\",\n" +
        "      \"components\": [\n" +
        "        {\n" +
        "          \"type\": \"block\",\n" +
        "          \"name\": \"Comp 1\",\n" +
        "          \"description\": \"Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block\",\n" +
        "          \"time\": 0,\n" +
        "          \"productNames\": [],\n" +
        "          \"cookingState\": \"NOT_REACHED\",\n" +
        "          \"colorOption\": \"RED\",\n" +
        "          \"position\": \"(-756.0; 468.2)\",\n" +
        "          \"size\": \"(480.0; 544.0)\"\n" +
        "        },\n" +
        "        {\n" +
        "          \"type\": \"horizontal_composed\",\n" +
        "          \"components\": [\n" +
        "            {\n" +
        "              \"type\": \"block\",\n" +
        "              \"name\": \"Comp 3\",\n" +
        "              \"description\": \"Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block\",\n" +
        "              \"time\": 0,\n" +
        "              \"productNames\": [],\n" +
        "              \"cookingState\": \"NOT_REACHED\",\n" +
        "              \"colorOption\": \"PINK\",\n" +
        "              \"position\": \"(-1428.0; 1204.2)\",\n" +
        "              \"size\": \"(480.0; 544.0)\"\n" +
        "            },\n" +
        "            {\n" +
        "              \"type\": \"horizontal_composed\",\n" +
        "              \"components\": [\n" +
        "                {\n" +
        "                  \"type\": \"block\",\n" +
        "                  \"name\": \"I am here! 2\",\n" +
        "                  \"description\": \"Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block\",\n" +
        "                  \"time\": 0,\n" +
        "                  \"productNames\": [],\n" +
        "                  \"cookingState\": \"NOT_REACHED\",\n" +
        "                  \"colorOption\": \"YELLOW\",\n" +
        "                  \"position\": \"(-756.0; 1204.2)\",\n" +
        "                  \"size\": \"(480.0; 544.0)\"\n" +
        "                },\n" +
        "                {\n" +
        "                  \"type\": \"block\",\n" +
        "                  \"name\": \"I am here! 5\",\n" +
        "                  \"description\": \"Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block\",\n" +
        "                  \"time\": 0,\n" +
        "                  \"productNames\": [],\n" +
        "                  \"cookingState\": \"NOT_REACHED\",\n" +
        "                  \"colorOption\": \"WHITE\",\n" +
        "                  \"position\": \"(-180.0; 1204.2)\",\n" +
        "                  \"size\": \"(480.0; 544.0)\"\n" +
        "                }\n" +
        "              ],\n" +
        "              \"position\": \"(-852.0; 1108.2)\"\n" +
        "            }\n" +
        "          ],\n" +
        "          \"position\": \"(-1524.0; 1012.2)\"\n" +
        "        },\n" +
        "        {\n" +
        "          \"type\": \"block\",\n" +
        "          \"name\": \"I am here! 6\",\n" +
        "          \"description\": \"Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block\",\n" +
        "          \"time\": 0,\n" +
        "          \"productNames\": [],\n" +
        "          \"cookingState\": \"NOT_REACHED\",\n" +
        "          \"colorOption\": \"ORANGE\",\n" +
        "          \"position\": \"(-756.0; 1940.2)\",\n" +
        "          \"size\": \"(480.0; 544.0)\"\n" +
        "        }\n" +
        "      ],\n" +
        "      \"position\": \"(-1620.0; 372.2)\"\n" +
        "    },\n" +
        "    {\n" +
        "      \"type\": \"vertical_composed\",\n" +
        "      \"components\": [\n" +
        "        {\n" +
        "          \"type\": \"block\",\n" +
        "          \"name\": \"Comp 2\",\n" +
        "          \"description\": \"Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block\",\n" +
        "          \"time\": 0,\n" +
        "          \"productNames\": [],\n" +
        "          \"cookingState\": \"NOT_REACHED\",\n" +
        "          \"colorOption\": \"RED\",\n" +
        "          \"position\": \"(1452.0; 564.2)\",\n" +
        "          \"size\": \"(480.0; 544.0)\"\n" +
        "        },\n" +
        "        {\n" +
        "          \"type\": \"horizontal_composed\",\n" +
        "          \"components\": [\n" +
        "            {\n" +
        "              \"type\": \"block\",\n" +
        "              \"name\": \"I am here! 1\",\n" +
        "              \"description\": \"Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block\",\n" +
        "              \"time\": 0,\n" +
        "              \"productNames\": [],\n" +
        "              \"cookingState\": \"NOT_REACHED\",\n" +
        "              \"colorOption\": \"YELLOW\",\n" +
        "              \"position\": \"(876.0; 1204.2)\",\n" +
        "              \"size\": \"(480.0; 544.0)\"\n" +
        "            },\n" +
        "            {\n" +
        "              \"type\": \"block\",\n" +
        "              \"name\": \"I am here! 3\",\n" +
        "              \"description\": \"Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block\",\n" +
        "              \"time\": 0,\n" +
        "              \"productNames\": [],\n" +
        "              \"cookingState\": \"NOT_REACHED\",\n" +
        "              \"colorOption\": \"PINK\",\n" +
        "              \"position\": \"(1452.0; 1204.2)\",\n" +
        "              \"size\": \"(480.0; 544.0)\"\n" +
        "            },\n" +
        "            {\n" +
        "              \"type\": \"block\",\n" +
        "              \"name\": \"I am here! 4\",\n" +
        "              \"description\": \"Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block\",\n" +
        "              \"time\": 0,\n" +
        "              \"productNames\": [],\n" +
        "              \"cookingState\": \"NOT_REACHED\",\n" +
        "              \"colorOption\": \"GREEN\",\n" +
        "              \"position\": \"(2028.0; 1204.2)\",\n" +
        "              \"size\": \"(480.0; 544.0)\"\n" +
        "            }\n" +
        "          ],\n" +
        "          \"position\": \"(780.0; 1108.2)\"\n" +
        "        },\n" +
        "        {\n" +
        "          \"type\": \"horizontal_composed\",\n" +
        "          \"components\": [\n" +
        "            {\n" +
        "              \"type\": \"block\",\n" +
        "              \"name\": \"I am here! 7\",\n" +
        "              \"description\": \"Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block\",\n" +
        "              \"time\": 0,\n" +
        "              \"productNames\": [],\n" +
        "              \"cookingState\": \"NOT_REACHED\",\n" +
        "              \"colorOption\": \"WHITE\",\n" +
        "              \"position\": \"(1452.0; 1844.2)\",\n" +
        "              \"size\": \"(480.0; 544.0)\"\n" +
        "            },\n" +
        "            {\n" +
        "              \"type\": \"block\",\n" +
        "              \"name\": \"I am here! 8\",\n" +
        "              \"description\": \"Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block Recipe Block\",\n" +
        "              \"time\": 0,\n" +
        "              \"productNames\": [],\n" +
        "              \"cookingState\": \"NOT_REACHED\",\n" +
        "              \"colorOption\": \"LIGHT_BLUE\",\n" +
        "              \"position\": \"(0.0; 0.0)\",\n" +
        "              \"size\": \"(480.0; 544.0)\"\n" +
        "            }\n" +
        "          ],\n" +
        "          \"position\": \"(0.0; 0.0)\"\n" +
        "        }\n" +
        "      ],\n" +
        "      \"position\": \"(684.0; 468.2)\"\n" +
        "    }\n" +
        "  ],\n" +
        "  \"position\": \"(-1716.0; 276.2)\"\n" +
        "}"