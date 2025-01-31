package com.letthemcook.core.domain.model.file

enum class FileType(val mimeType: String) {
    IMAGE("image/jpeg"),
    VIDEO("video/mp4"),
    ANY("*/");
}