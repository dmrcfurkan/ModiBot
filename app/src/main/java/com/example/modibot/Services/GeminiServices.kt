package com.example.modibot.Services



data class RequestBody(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)
data class YourResponseModel(
    val result: String // API'den dönen yanıtı modelleyin
)