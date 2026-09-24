package com.example.ui.library

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AssetBitmapImage
import com.example.ui.design.TaskIdsColors
import java.io.File
import java.io.FileOutputStream

data class LibraryActivity(
    val id: String,
    val title: String,
    val category: String,
    val age: String,
    val duration: String,
    val summary: String,
    val materials: List<String>,
    val steps: List<String>
)

object ParentLibraryCatalog {
    val activities = listOf(
        LibraryActivity(
            id = "caca_cores",
            title = "Caça às cores",
            category = "Brincadeira",
            age = "4-8 anos",
            duration = "15-25 min",
            summary = "Uma missão simples para observar o ambiente, movimentar o corpo e treinar atenção.",
            materials = listOf("Folha impressa", "Lápis ou giz de cera"),
            steps = listOf(
                "Escolha 5 cores para procurar pela casa.",
                "A criança encontra um objeto de cada cor.",
                "Marque cada descoberta no quadro.",
                "No final, conversem sobre qual cor foi mais fácil e mais difícil."
            )
        ),
        LibraryActivity(
            id = "bingo_rotina",
            title = "Bingo da rotina",
            category = "Rotina",
            age = "5-10 anos",
            duration = "Durante o dia",
            summary = "Transforma pequenas responsabilidades em uma sequência visual de conquistas.",
            materials = listOf("Cartela impressa", "Canetinha"),
            steps = listOf(
                "Escolha 9 tarefas simples da rotina.",
                "Preencha a cartela 3x3.",
                "A cada tarefa concluída, marque um espaço.",
                "Ao completar uma linha, comemore com uma recompensa combinada."
            )
        ),
        LibraryActivity(
            id = "emocoes",
            title = "Cartas das emoções",
            category = "Emoções",
            age = "4-10 anos",
            duration = "10-20 min",
            summary = "Ajuda a criança a nomear sentimentos e conversar sobre situações do dia.",
            materials = listOf("Cartas impressas", "Tesoura sem ponta"),
            steps = listOf(
                "Recorte as cartas de emoções.",
                "Escolha uma carta por vez.",
                "Conte uma situação em que sentiu aquela emoção.",
                "Pensem juntos em uma forma saudável de lidar com ela."
            )
        ),
        LibraryActivity(
            id = "sem_tela",
            title = "Desafio sem tela",
            category = "Movimento",
            age = "5-12 anos",
            duration = "30 min",
            summary = "Sequência de desafios físicos e criativos para reduzir tempo de tela com leveza.",
            materials = listOf("Cronômetro", "Espaço seguro"),
            steps = listOf(
                "Faça 10 pulos.",
                "Monte uma torre com objetos seguros.",
                "Desenhe algo que existe na sala.",
                "Conte uma história usando três objetos encontrados."
            )
        ),
        LibraryActivity(
            id = "alfabeto_movimento",
            title = "Alfabeto em movimento",
            category = "Aprendizagem",
            age = "5-8 anos",
            duration = "20 min",
            summary = "Une alfabetização, movimento e associação de palavras.",
            materials = listOf("Folha impressa", "Lápis"),
            steps = listOf(
                "Escolha 5 letras.",
                "Para cada letra, encontre um objeto que comece com ela.",
                "Escreva ou desenhe o objeto.",
                "Repita falando o som inicial da palavra."
            )
        ),
        LibraryActivity(
            id = "missao_organizacao",
            title = "Missão organização",
            category = "Autonomia",
            age = "5-11 anos",
            duration = "15 min",
            summary = "Organização em formato de missão por etapas curtas e objetivas.",
            materials = listOf("Checklist impresso", "Caixas ou cestos"),
            steps = listOf(
                "Escolha um único espaço para organizar.",
                "Separe objetos por categoria.",
                "Guarde primeiro os itens maiores.",
                "Marque cada etapa concluída no checklist."
            )
        )
    )
}

@Composable
fun ParentLibraryContent(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("Todos") }
    var message by remember { mutableStateOf<String?>(null) }
    val categories = listOf("Todos") + ParentLibraryCatalog.activities.map { it.category }.distinct()
    val visible = if (selectedCategory == "Todos") {
        ParentLibraryCatalog.activities
    } else {
        ParentLibraryCatalog.activities.filter { it.category == selectedCategory }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF8FBFF), Color(0xFFF4F6FF), Color(0xFFF9FAFF))
                )
            )
            .padding(14.dp)
    ) {
        LibraryHero()

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val selected = selectedCategory == category
                Text(
                    category,
                    color = if (selected) Color.White else TaskIdsColors.Ink,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(
                            if (selected) TaskIdsColors.Ink else Color.White,
                            RoundedCornerShape(18.dp)
                        )
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                )
            }
        }

        if (message != null) {
            Spacer(Modifier.height(10.dp))
            Text(
                message!!,
                color = TaskIdsColors.Green,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(visible) { activity ->
                LibraryActivityCard(
                    activity = activity,
                    useGirl = ParentLibraryCatalog.activities.indexOf(activity) % 2 == 0,
                    onDownload = {
                        val result = LibraryPdfGenerator.saveToDownloads(context, activity)
                        message = result.fold(
                            onSuccess = { "PDF salvo em Downloads/TASKIDS: $it" },
                            onFailure = { "Não foi possível gerar o PDF: ${it.message ?: "erro"}" }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun LibraryHero() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .shadow(10.dp, RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color.White, Color(0xFFEAF5FF), Color(0xFFF1ECFF))
                ),
                RoundedCornerShape(28.dp)
            )
            .padding(start = 22.dp, top = 18.dp, bottom = 18.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                "Biblioteca para famílias",
                color = TaskIdsColors.Ink,
                fontSize = 25.sp,
                lineHeight = 27.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Brincadeiras, atividades e materiais para transformar momentos simples em experiências especiais.",
                color = TaskIdsColors.Muted,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }

        Box(
            modifier = Modifier
                .width(210.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.BottomCenter
        ) {
            AssetBitmapImage(
                assetName = "avatar_boy_base.png",
                contentDescription = null,
                modifier = Modifier
                    .width(105.dp)
                    .fillMaxHeight()
                    .align(Alignment.BottomStart)
            )
            AssetBitmapImage(
                assetName = "avatar_girl_base.png",
                contentDescription = null,
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun LibraryActivityCard(
    activity: LibraryActivity,
    useGirl: Boolean,
    onDownload: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(165.dp)
            .shadow(7.dp, RoundedCornerShape(24.dp))
            .background(Color.White, RoundedCornerShape(24.dp))
    ) {
        Box(
            modifier = Modifier
                .width(135.dp)
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(
                        if (useGirl) {
                            listOf(Color(0xFFFFEEF5), Color(0xFFF0ECFF))
                        } else {
                            listOf(Color(0xFFEAF5FF), Color(0xFFE9F8F2))
                        }
                    ),
                    RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            AssetBitmapImage(
                assetName = if (useGirl) "avatar_girl_base.png" else "avatar_boy_base.png",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 8.dp, start = 6.dp, end = 6.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Text(
                activity.category.uppercase(),
                color = TaskIdsColors.Purple,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(Modifier.height(2.dp))

            Text(
                activity.title,
                color = TaskIdsColors.Ink,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                "${activity.age} • ${activity.duration}",
                color = TaskIdsColors.Muted,
                fontSize = 10.sp
            )

            Spacer(Modifier.height(7.dp))

            Text(
                activity.summary,
                color = TaskIdsColors.Muted,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                maxLines = 2
            )

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${activity.materials.size} materiais",
                    color = TaskIdsColors.Muted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "BAIXAR PDF",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier
                        .background(TaskIdsColors.Ink, RoundedCornerShape(14.dp))
                        .clickable(onClick = onDownload)
                        .padding(horizontal = 13.dp, vertical = 9.dp)
                )
            }
        }
    }
}

object LibraryPdfGenerator {
    fun saveToDownloads(context: Context, activity: LibraryActivity): Result<String> = runCatching {
        val safeName = activity.title
            .lowercase()
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
        val fileName = "taskids_${safeName}.pdf"
        val document = PdfDocument()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            isAntiAlias = true
            textSize = 26f
            isFakeBoldText = true
        }
        val sectionPaint = Paint().apply {
            isAntiAlias = true
            textSize = 16f
            isFakeBoldText = true
        }
        val bodyPaint = Paint().apply {
            isAntiAlias = true
            textSize = 13f
        }

        var y = 60f
        canvas.drawText("TASKIDS", 48f, y, titlePaint)
        y += 42f
        canvas.drawText(activity.title, 48f, y, titlePaint)
        y += 30f
        canvas.drawText("${activity.category}  •  ${activity.age}  •  ${activity.duration}", 48f, y, bodyPaint)
        y += 34f

        y = drawWrapped(canvas, activity.summary, 48f, y, 500f, bodyPaint)
        y += 22f

        canvas.drawText("Materiais", 48f, y, sectionPaint)
        y += 24f
        activity.materials.forEach {
            canvas.drawText("• $it", 60f, y, bodyPaint)
            y += 22f
        }

        y += 12f
        canvas.drawText("Como brincar", 48f, y, sectionPaint)
        y += 26f
        activity.steps.forEachIndexed { index, step ->
            y = drawWrapped(canvas, "${index + 1}. $step", 60f, y, 470f, bodyPaint)
            y += 14f
        }

        y += 22f
        canvas.drawText("Dica para os responsáveis", 48f, y, sectionPaint)
        y += 24f
        y = drawWrapped(
            canvas,
            "Adapte a atividade à idade, ao ritmo e às necessidades da criança. O objetivo é criar uma experiência positiva, não uma competição.",
            48f,
            y,
            500f,
            bodyPaint
        )

        document.finishPage(page)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/TASKIDS")
            }
            val uri = context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                values
            ) ?: error("Não foi possível criar o arquivo.")
            context.contentResolver.openOutputStream(uri)?.use { document.writeTo(it) }
                ?: error("Não foi possível abrir o destino.")
        } else {
            val dir = context.getExternalFilesDir("TASKIDS") ?: context.filesDir
            if (!dir.exists()) dir.mkdirs()
            FileOutputStream(File(dir, fileName)).use { document.writeTo(it) }
        }

        document.close()
        fileName
    }

    private fun drawWrapped(
        canvas: android.graphics.Canvas,
        text: String,
        x: Float,
        startY: Float,
        maxWidth: Float,
        paint: Paint
    ): Float {
        var y = startY
        var line = ""
        text.split(" ").forEach { word ->
            val candidate = if (line.isBlank()) word else "$line $word"
            if (paint.measureText(candidate) > maxWidth && line.isNotBlank()) {
                canvas.drawText(line, x, y, paint)
                y += 19f
                line = word
            } else {
                line = candidate
            }
        }
        if (line.isNotBlank()) {
            canvas.drawText(line, x, y, paint)
            y += 19f
        }
        return y
    }
}
