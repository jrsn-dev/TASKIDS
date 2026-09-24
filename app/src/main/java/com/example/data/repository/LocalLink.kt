package com.example.data.repository

import com.example.model.Child
import com.example.model.Reward
import com.example.model.Routine
import com.example.model.Task
import com.example.model.TaskStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.URL
import java.security.SecureRandom

/** A deliberate, one-profile-at-a-time transfer for devices on the same Wi-Fi network. */
class LocalLink(private val repository: TaskRepository) {
    val code: String = SecureRandom().nextInt(1_000_000).toString().padStart(6, '0')
    val address: String get() = NetworkInterface.getNetworkInterfaces()?.toList()
        ?.asSequence()?.filter { it.isUp && !it.isLoopback }
        ?.flatMap { it.inetAddresses.toList().asSequence() }
        ?.filterIsInstance<Inet4Address>()?.firstOrNull { it.isSiteLocalAddress }
        ?.hostAddress ?: "Sem Wi-Fi local"

    private var server: ServerSocket? = null
    fun close() { runCatching { server?.close() } }

    suspend fun serve(selectedChildId: () -> Long, onImported: (Long) -> Unit) = withContext(Dispatchers.IO) {
        val socket = ServerSocket(8765)
        server = socket
        try {
            while (!socket.isClosed) {
                val peer = try { socket.accept() } catch (_: java.net.SocketException) { break }
                peer.use { client ->
                    client.soTimeout = 5_000
                    runCatching {
                        val input = client.getInputStream().buffered()
                        val request = input.readLineAscii(1024)
                        val tokens = request.split(' ')
                        require(tokens.size >= 2)
                        val headers = mutableMapOf<String, String>()
                        while (true) {
                            val line = input.readLineAscii(1024)
                            if (line.isEmpty()) break
                            val idx = line.indexOf(':')
                            if (idx > 0) headers[line.substring(0, idx).lowercase()] = line.substring(idx + 1).trim()
                        }
                        if (headers["x-taskids-code"] != code) {
                            client.reply(403, "Código incorreto")
                        } else when (tokens[0] to tokens[1]) {
                            "GET" to "/ping" -> client.reply(200, "TASKIDS")
                            "GET" to "/profile" -> client.reply(200, export(selectedChildId()))
                            "POST" to "/profile" -> {
                                val length = headers["content-length"]?.toIntOrNull() ?: 0
                                require(length in 1..262_144)
                                val bytes = ByteArray(length)
                                var offset = 0
                                while (offset < length) {
                                    val count = input.read(bytes, offset, length - offset)
                                    require(count > 0)
                                    offset += count
                                }
                                val importedId = importProfile(String(bytes, Charsets.UTF_8))
                                onImported(importedId)
                                client.reply(200, "Perfil recebido")
                            }
                            else -> client.reply(404, "Não encontrado")
                        }
                    }.onFailure { runCatching { client.reply(400, "Requisição inválida") } }
                }
            }
        } finally { server = null; socket.close() }
    }

    suspend fun ping(host: String, pin: String): String = request(host, pin, "GET", "/ping")
    suspend fun pull(host: String, pin: String): Long = importProfile(request(host, pin, "GET", "/profile"))
    suspend fun push(host: String, pin: String, childId: Long): String =
        request(host, pin, "POST", "/profile", export(childId))

    private suspend fun request(host: String, pin: String, method: String, path: String, body: String? = null): String =
        withContext(Dispatchers.IO) {
            require(pin.matches(Regex("[0-9]{6}"))) { "Digite os 6 dígitos exibidos na TV." }
            require(host.matches(Regex("[0-9.]+")) && host.split('.').size == 4) { "Digite o endereço IPv4 da TV." }
            require(InetAddress.getByName(host).isSiteLocalAddress) { "Use o endereço da rede Wi-Fi local." }
            val url = URL("http://$host:8765$path")
            val conn = url.openConnection() as HttpURLConnection
            try {
                conn.connectTimeout = 4000
                conn.readTimeout = 6000
                conn.requestMethod = method
                conn.setRequestProperty("X-Taskids-Code", pin)
                if (body != null) {
                    conn.doOutput = true
                    conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
                }
                val status = conn.responseCode
                val answer = (if (status in 200..299) conn.inputStream else conn.errorStream)
                    ?.bufferedReader()?.use { it.readText() } ?: "Sem resposta"
                check(status in 200..299) { answer }
                answer
            } finally { conn.disconnect() }
        }

    suspend fun export(childId: Long): String {
        val child = repository.childById(childId) ?: error("Selecione um perfil.")
        val tasks = repository.listTasks(childId)
        val rewards = repository.listRewards(childId)
        val routines = repository.listRoutines(childId)
        return JSONObject().apply {
            put("version", 1)
            put("child", JSONObject().apply {
                put("name", child.name); put("stars", child.totalStars); put("xp", child.totalXp)
                put("character", child.avatarCharacter); put("theme", child.gameTheme)
                put("streak", child.currentStreak)
            })
            put("tasks", JSONArray().apply { tasks.forEach { t -> put(JSONObject().apply {
                put("title", t.title); put("description", t.description); put("minutes", t.durationMinutes)
                put("iconKey", t.iconKey); put("order", t.orderIndex); put("stars", t.rewardStars)
                put("xp", t.rewardXp); put("active", t.isActive); put("status", t.status.name)
            }) } })
            put("rewards", JSONArray().apply { rewards.forEach { r -> put(JSONObject().apply {
                put("title", r.title); put("description", r.description); put("cost", r.costStars)
                put("type", r.type); put("icon", r.icon)
            }) } })
            put("routines", JSONArray().apply { routines.forEach { r -> put(JSONObject().apply {
                put("title", r.title); put("time", r.startTime ?: ""); put("days", r.daysCsv)
                put("icon", r.icon)
            }) } })
        }.toString()
    }

    suspend fun importProfile(payload: String): Long {
        require(payload.toByteArray().size <= 262_144)
        val json = JSONObject(payload)
        require(json.getInt("version") == 1) { "Versão incompatível." }
        val profile = json.getJSONObject("child")
        val name = profile.getString("name").trim().take(40)
        require(name.isNotEmpty())
        val existing = repository.findActiveChild(name)
        val base = existing ?: Child(name = name)
        val childId = repository.upsertChild(base.copy(
            name = name, totalStars = profile.optInt("stars").coerceAtLeast(0),
            totalXp = profile.optInt("xp").coerceAtLeast(0),
            avatarCharacter = profile.optString("character", "BOY"),
            gameTheme = profile.optString("theme", "SKY"),
            currentStreak = profile.optInt("streak").coerceAtLeast(0)
        )).let { if (existing != null) existing.id else it }
        val currentTasks = repository.listTasks(childId)
        val tasks = json.getJSONArray("tasks")
        require(tasks.length() <= 200)
        val titles = mutableSetOf<String>()
        for (i in 0 until tasks.length()) {
            val item = tasks.getJSONObject(i)
            val title = item.getString("title").trim().take(60)
            require(title.isNotEmpty())
            titles += title
            val previous = currentTasks.firstOrNull { it.title == title }
            repository.insertTask((previous ?: Task(childId = childId, title = title, durationMinutes = 15, orderIndex = i)).copy(
                title = title, description = item.optString("description").take(250),
                durationMinutes = item.optInt("minutes", 15).coerceIn(1, 240),
                iconKey = item.optString("iconKey", "GENERIC"), orderIndex = item.optInt("order", i),
                rewardStars = item.optInt("stars", 10).coerceIn(1, 100), rewardXp = item.optInt("xp", 100).coerceIn(10, 1000),
                isActive = item.optBoolean("active", true),
                status = runCatching { TaskStatus.valueOf(item.optString("status")) }.getOrDefault(TaskStatus.PENDING)
            ))
        }
        currentTasks.filter { it.title !in titles }.forEach { repository.deleteTask(it) }
        val currentRewards = repository.listRewards(childId)
        val rewards = json.getJSONArray("rewards")
        require(rewards.length() <= 100)
        val rewardTitles = mutableSetOf<String>()
        for (i in 0 until rewards.length()) {
            val item = rewards.getJSONObject(i)
            val title = item.getString("title").trim().take(60)
            rewardTitles += title
            val previous = currentRewards.firstOrNull { it.title == title }
            repository.upsertReward((previous ?: Reward(childId = childId, title = title, costStars = 50)).copy(
                title = title, description = item.optString("description").take(250),
                costStars = item.optInt("cost", 50).coerceIn(1, 10000), type = item.optString("type", "CUSTOM"),
                icon = item.optString("icon", "🎁")
            ))
        }
        currentRewards.filter { it.childId == childId && it.title !in rewardTitles }.forEach { repository.deleteReward(it) }
        val currentRoutines = repository.listRoutines(childId)
        val routines = json.getJSONArray("routines")
        require(routines.length() <= 100)
        val routineTitles = mutableSetOf<String>()
        for (i in 0 until routines.length()) {
            val item = routines.getJSONObject(i)
            val title = item.getString("title").trim().take(60)
            routineTitles += title
            val previous = currentRoutines.firstOrNull { it.title == title }
            repository.upsertRoutine((previous ?: Routine(childId = childId, title = title)).copy(
                title = title, startTime = item.optString("time").ifBlank { null },
                daysCsv = item.optString("days", "1,2,3,4,5"), icon = item.optString("icon", "🌟")
            ))
        }
        currentRoutines.filter { it.title !in routineTitles }.forEach { repository.deleteRoutine(it) }
        return childId
    }
}

private fun java.io.BufferedInputStream.readLineAscii(limit: Int): String {
    val bytes = ArrayList<Byte>()
    while (bytes.size < limit) {
        val b = read()
        if (b < 0 || b == 10) break
        if (b != 13) bytes.add(b.toByte())
    }
    require(bytes.size < limit)
    return bytes.toByteArray().toString(Charsets.US_ASCII)
}

private fun java.net.Socket.reply(status: Int, body: String) {
    val bytes = body.toByteArray(Charsets.UTF_8)
    getOutputStream().write("HTTP/1.1 $status ${if (status == 200) "OK" else "Error"}\r\nContent-Type: text/plain; charset=utf-8\r\nContent-Length: ${bytes.size}\r\nConnection: close\r\n\r\n".toByteArray(Charsets.US_ASCII))
    getOutputStream().write(bytes)
    getOutputStream().flush()
}
