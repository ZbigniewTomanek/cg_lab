import lab1.BitmapGenerator
import lab1.BitmapGenerator.Companion.addPatternOnImage
import lab1.BitmapGenerator.Companion.generateBrettPattern
import lab1.BitmapGenerator.Companion.generateCircle
import lab1.BitmapGenerator.Companion.generateConcentricCircles
import lab1.BitmapGenerator.Companion.generateFadingCircle
import lab1.BitmapGenerator.Companion.generateManySmallCircles

fun main(args: Array<String>) {
//    val resX = args[0].trim()
//    val resY = args[1].trim()

    val resX = 600
    val resY = 600

    generateBrettPattern(resX.toInt(), resY.toInt())

    addPatternOnImage("dog.jpg", ::generateCircle)

    generateManySmallCircles(400, 400)
    generateConcentricCircles(400, 400, 50, 10)
}