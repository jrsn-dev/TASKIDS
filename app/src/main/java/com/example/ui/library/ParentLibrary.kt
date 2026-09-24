package com.example.ui.library

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.design.TaskIdsColors
import com.example.R
import com.example.ui.game.GameIcon
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

    Column(modifier = modifier) {
        Text(
            "Biblioteca para famílias",
            color = TaskIdsColors.Ink,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            "Brincadeiras, atividades imprimíveis e materiais para usar em casa.",
            color = TaskIdsColors.Muted,
            fontSize = 13.sp
        )

        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.take(5).forEach { category ->
                Text(
                    category,
                    color = if (selectedCategory == category) Color.White else TaskIdsColors.Ink,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(
                            if (selectedCategory == category) TaskIdsColors.Blue else TaskIdsColors.SoftBg,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        if (message != null) {
            Spacer(Modifier.height(10.dp))
            Text(
                message!!,
                color = TaskIdsColors.Green,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(visible) { activity ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val artColor = when (activity.category) {
                            "Brincadeira" -> TaskIdsColors.Pink
                            "Rotina" -> TaskIdsColors.Blue
                            "Emoções" -> TaskIdsColors.Purple
                            "Movimento" -> TaskIdsColors.Green
                            else -> TaskIdsColors.Orange
                        }
                        val artKey = when (activity.category) {
                            "Brincadeira" -> "TOYS"
                            "Rotina", "Autonomia" -> "HOMEWORK"
                            "Movimento" -> "SPORT"
                            else -> "BOOK"
                        }
                        Box(Modifier.size(74.dp).background(artColor.copy(alpha = 0.14f),
                            RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
                            GameIcon(artKey, Modifier.size(53.dp), artColor)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                activity.category.uppercase(),
                                color = TaskIdsColors.Blue,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                activity.title,
                                color = TaskIdsColors.Ink,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                "${activity.age} • ${activity.duration}",
                                color = TaskIdsColors.Muted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(activity.summary, color = TaskIdsColors.Muted, fontSize = 12.sp,
                            modifier = Modifier.weight(1f), maxLines = 2)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "BAIXAR PDF",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .background(TaskIdsColors.Purple, RoundedCornerShape(14.dp))
                                .clickable {
                                    val result = LibraryPdfGenerator.saveToDownloads(context, activity)
                                    message = result.fold(
                                        onSuccess = { "PDF salvo em Downloads/TASKIDS: $it" },
                                        onFailure = { "Não foi possível gerar o PDF: ${it.message ?: "erro"}" }
                                    )
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }

                }
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
        val logo = BitmapFactory.decodeResource(context.resources, R.drawable.taskids_logo_original)
        canvas.drawBitmap(logo, null, RectF(48f, 35f, 238f, 79f), null)
        logo.recycle()
        y += 68f
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
