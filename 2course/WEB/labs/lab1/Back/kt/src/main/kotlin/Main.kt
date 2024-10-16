import com.fastcgi.FCGIInterface
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.measureNanoTime


fun main() {
    var fcgiInterface = FCGIInterface()
    while (fcgiInterface.FCGIaccept() >= 0) {
        // Получаем метод запроса
        val requestMethod = FCGIInterface.request.params.getProperty("REQUEST_METHOD")
        var content: String

        val start = System.nanoTime()

        // Проверяем, что это GET-запрос
        if (requestMethod == "GET") {


            // Получаем параметры из строки запроса
            val queryString = FCGIInterface.request.params.getProperty("QUERY_STRING")
            if (queryString != null) {

                // Преобразуем в объект Date
                val currentTimeMillis = System.currentTimeMillis()

                // Обработка параметров
                val params: HashMap<String, String> = getValues(queryString)
                val x = params["x"].toString()
                val y = params["y"].toString()
                val r = params["r"].toString()

                try {
                    if(validateValuesXYR(x, y, r)){
                        if (script(x.toInt(), y.toFloat(), r.toFloat())) {
                            content = response(true, x, y, r, start)
                        } else {
                            content = response(false, x, y, r, start)
                        }
                        println(content)
                    } else {
                     println(errorResponse("Not valid data"))
                    }
                } catch (e: Exception) {
                    println(errorResponse("Not valid data"))
                }
            } else {
                println(errorResponse("Query is null"))
            }
        } else {
            println(errorResponse("Not valid http method"))
        }
    }
}

fun validateValuesXYR(x: String?, y: String?, r: String?): Boolean {
    val xNum = x?.toInt()!!
    val yNum = y?.toFloat()!!
    val rNum = r?.toFloat()
    return (intArrayOf(-3, -2, -1, 0, 1, 2, 3, 4, 5).contains(xNum)) && (-3 <= yNum) && (yNum <= 5) && (arrayOf(
        1f,
        1.5f,
        2f,
        2.5f,
        3f
    ).contains(rNum))
}

fun script(x: Int, y: Float, r: Float): Boolean {
    val areaCircle = ((x * x + y * y) <= r * r) && (x <= 0) && (y >= 0)
    val areaTriangle = (x <= 0) && (y <= 0) && (y >= -x - r)
    val areaReactangle = (y <= 0) && y <= (-r) && (x >= 0) && (x <= r / 2)
    return areaCircle || areaTriangle || areaReactangle
}

private fun getValues(inpString: String): LinkedHashMap<String, String> {
    val args = inpString.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val values = LinkedHashMap<String, String>()
    for (s in args) {
        val arg = s.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        values[arg[0]] = arg[1]
    }
    return values
}

private fun response(isHit: Boolean, x: String, y: String, r: String, startTime: Long): String {
    val endTime = System.nanoTime()
    val duration = (endTime - startTime)
    val scrTime = "$duration" // в наносекундах

    val sdf = SimpleDateFormat("dd.MM.yy HH:mm:ss")
    val currentDateTime = sdf.format(Date())
    val content = """
                {"result":"%s","x":"%s","y":"%s","r":"%s","currTime":"%s","scrTime":"%s","error":"not"}
                
                """.trimIndent().format(isHit, x, y, r, currentDateTime, scrTime)
    return """
                HTTP/1.1 200 OK
                Content-Type: application/json
                Content-Length: %d
                
                %s
                """.trimIndent().format(content.toByteArray(StandardCharsets.UTF_8).size, content)
}

private fun errorResponse(msg: String): String {
    val content = """{"error":"$msg"}"""
    return """
                HTTP/1.1 400 Bad Request
                Content-Type: application/json
                Content-Length: %d
               
                %s
                """.trimIndent().format(content.toByteArray(StandardCharsets.UTF_8).size, content)
}