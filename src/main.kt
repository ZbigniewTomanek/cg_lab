import lab1.BitmapGenerator.addPatternOnImage
import lab1.BitmapGenerator.drawJapanFlag
import lab1.BitmapGenerator.generateBrettPattern
import lab1.BitmapGenerator.generateCheekPattern
import lab1.BitmapGenerator.generateCircle
import lab1.BitmapGenerator.generateConcentricCircles
import lab1.BitmapGenerator.generateManySmallCircles
import lab1.BitmapGenerator.generateRotatedBrett
import lab1.BitmapGenerator.mix2Images
import lab1.BitmapGenerator.red

fun main(args: Array<String>) {
//    val resX = args[0].trim()
//    val resY = args[1].trim()

    val resX = 600
    val resY = 600

    generateBrettPattern(resX.toInt(), resY.toInt())

    addPatternOnImage("cat.jpg", ::generateCircle)

    generateCheekPattern(600, 600)
    generateRotatedBrett(600, 600)
    generateManySmallCircles(400, 400)
    generateConcentricCircles(600, 600)
    drawJapanFlag(600, 600, secondaryColor = red)
    mix2Images("cat.jpg", "dog.jpg", ::generateBrettPattern)
}