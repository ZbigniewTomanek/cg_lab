package lab1

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.sqrt




object BitmapGenerator {
    private val ringWidth = 10
    private val blurFactor = 2

    val black = intToRGB(0, 0, 0)
    val white = intToRGB(255, 255, 255)
    val red = intToRGB(255, 0, 0)
    val gray = intToRGB(126, 126, 126)

    fun generateCircle(xRes: Int, yRes: Int, primaryColor: Int= white, secondaryColor: Int=black): BufferedImage {
        val image = BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_RGB)

        val xCenter = xRes / 2
        val yCenter = yRes / 2

        var distance: Double
        var ringIndex: Int
        for (i in 0 until yRes) {
            for (j in 0 until xRes) {
                distance = countDistance(j, i, xCenter, yCenter)
                ringIndex = distance.toInt() / ringWidth

                if (ringIndex % 2 == 0)
                    image.setRGB(j, i, secondaryColor)
                else
                    image.setRGB(j, i, primaryColor)
            }
        }

        saveBitmap(image, "circle")

        return image
    }

    fun generateFadingCircle(xRes: Int, yRes: Int, primaryColor: Int = white, secondaryColor: Int = black): BufferedImage {
        val image = BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_RGB)
        val xCenter = xRes / 2
        val yCenter = xRes / 2

        val maxDistance = countDistance(xCenter, yCenter, xRes, yRes)
        var distance: Double
        var ringIndex: Int
        var gray: Int
        for (i in 0 until yRes) {
            for (j in 0 until xRes) {
                distance = countDistance(j, i, xCenter, yCenter)
                ringIndex = distance.toInt() / ringWidth

                if (ringIndex % 2 == 0)
                    image.setRGB(j, i, black)
                else {
                    gray = 255 - ((distance / maxDistance) * blurFactor * 255).toInt()
                    image.setRGB(j, i, intToRGB(gray, gray, gray))
                }
            }
        }

        saveBitmap(image, "fading_circle")

        return image
    }

    fun generateCheekPattern(xRes: Int, yRes: Int, primaryColor: Int=white, secondaryColor: Int=black): BufferedImage {
        val image = BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_RGB)

        for (i in 0 until yRes) {
            for (j in 0 until xRes) {
                image.setRGB(j, i, primaryColor)
            }
        }

        val lineWidth = 10
        val cheekWidth = 20

        val numOfCols = xRes / (lineWidth + cheekWidth)
        val numOfRows = yRes / (lineWidth+cheekWidth)

        for (i in 1 until numOfCols) {
            val x = (i / numOfCols.toDouble()) * xRes
            drawColumn(image, x.toInt(), lineWidth, secondaryColor)
        }

        for (i in 1 until numOfRows) {
            val y = (i / numOfRows.toDouble()) * yRes
            drawRow(image, y.toInt(), lineWidth, secondaryColor)
        }


        saveBitmap(image, "cheek")

        return image
    }

    fun generateBrettPattern(xRes: Int, yRes: Int, primaryColor: Int= white, secondaryColor: Int=black): BufferedImage {
        val image = BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_RGB)

        val squareSize = 20
        val squaresX = xRes / squareSize
        val squaresY = yRes / squareSize

        var wasBlack = true

        for (i in 0 until squaresY) {
            wasBlack = !wasBlack
            for (j in 0 until squaresX) {

                val y = i * squareSize
                val x = j * squareSize

                for (ii in y until y + squareSize) {
                    for (jj in x until x + squareSize) {

                        if (wasBlack) {
                            image.setRGB(jj, ii, primaryColor)
                        } else {
                            image.setRGB(jj, ii, secondaryColor)
                        }
                    }
                }

                wasBlack = !wasBlack
            }
        }

        saveBitmap(image, "brett")

        return image
    }

    fun generateRotatedBrett(xRes: Int, yRes: Int, primaryColor: Int = white, secondaryColor: Int = black): BufferedImage {
        val image = BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_RGB)

        val diagonalLength = 40
        var currentLength = diagonalLength
        var descending = true


        for (i in 0 until yRes) {
            var whiteInRow = currentLength
            var blackInRow = diagonalLength - currentLength + 1

            for (j in 0 until xRes) {
                when {
                    whiteInRow > -1 -> {
                        image.setRGB(j, i, white)
                        whiteInRow--
                    }
                    blackInRow > -(diagonalLength - currentLength - 1) -> {
                        image.setRGB(j, i, black)
                        blackInRow--
                    }
                    else -> {
                        whiteInRow = 2 * currentLength
                        blackInRow = diagonalLength - currentLength + 1
                    }
                }
            }

            if (currentLength == -1)
                descending = false
            if (currentLength == diagonalLength)
                descending = true
            if (descending)
                currentLength--
            else
                currentLength++
        }

        saveBitmap(image, "rotated_brett")

        return image
    }

    fun addPatternOnImage(filename: String, generatePattern: (Int, Int, Int, Int) -> BufferedImage, primaryColor: Int = white, secondaryColor: Int= black): BufferedImage {
        val image = ImageIO.read(File(filename)) ?: throw IllegalArgumentException("Wrong filename")
        val patternBitmap = generatePattern(image.width, image.height, primaryColor, secondaryColor)

        var maskColor: Int
        var imageColor: Int
        for (i in 0 until image.height) {
            for (j in 0 until  image.width) {
                maskColor = patternBitmap.getRGB(j, i)
                imageColor = image.getRGB(j, i)

                image.setRGB(j, i, maskColor and imageColor)
            }
        }

        saveBitmap(image, filename+"_altered")

        return image
    }

    fun generateManySmallCircles(xRes: Int, yRes: Int, primaryColor: Int= white, secondaryColor: Int= black): BufferedImage {
        val circleRadius:Int=20
        val circlesDistance:Int=10

        val image = BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_RGB)
        for (i in 0 until yRes) {
            for (j in 0 until xRes) {
                image.setRGB(j, i, primaryColor)
            }
        }

        val centerDistance = 2 * circleRadius + circlesDistance

        val ticksX = xRes / centerDistance
        val ticksY = yRes / centerDistance

        for (i in 0 .. ticksY) {
            for (j in 0 .. ticksX) {
                val posX = (j.toDouble() / ticksX) * xRes
                val posY = (i.toDouble() / ticksY) * yRes

                drawCircle(image, posX.toInt(), posY.toInt(), circleRadius, secondaryColor)
            }
        }

        saveBitmap(image, "circles")

        return image
    }

    fun generateConcentricCircles
                (xRes: Int, yRes: Int, primaryColor: Int= white, secondaryColor: Int= black): BufferedImage {
        val circleRadius:Int=20
        val ringWidth: Int=5

        val image = BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_RGB)
        for (i in 0 until yRes) {
            for (j in 0 until xRes) {
                image.setRGB(j, i, primaryColor)
            }
        }

        val ticksX = xRes / (2 * circleRadius)
        val ticksY = yRes / (2 * circleRadius)

        for (i in 0 .. ticksY) {
            for (j in 0 .. ticksX) {
                val posX = (j.toDouble() / ticksX) * xRes + circleRadius
                val posY = (i.toDouble() / ticksY) * yRes + circleRadius

                drawConcentricCircle(image, posX.toInt(), posY.toInt(), circleRadius, ringWidth, primaryColor, secondaryColor)
            }
        }

        saveBitmap(image, "concentric_circles")

        return image
    }

    fun drawJapanFlag(xRes: Int, yRes: Int, primaryColor: Int = white, secondaryColor: Int = black): BufferedImage {
        val image = BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_RGB)

        val degree = 30

        val xCenter = xRes / 2
        val yCenter = yRes / 2

        var xShifted: Int
        var yShifted: Int
        var fi: Double
        for (i in 0 until yRes) {
            for (j in 0 until xRes) {
                xShifted = j - xCenter
                yShifted = i - yCenter

                fi = Math.toDegrees(atan2(yShifted.toDouble(), xShifted.toDouble()))

                if (fi < 0) {
                    if ((fi.toInt() / degree) % 2 == 0) {
                        image.setRGB(j, i, primaryColor)
                    } else {
                        image.setRGB(j, i, secondaryColor)
                    }
                } else {
                    if ((fi.toInt() / degree) % 2 == 1) {
                        image.setRGB(j, i, primaryColor)
                    } else {
                        image.setRGB(j, i, secondaryColor)
                    }
                }

            }
        }

        saveBitmap(image, "japan")
        return image
    }

    fun mix2Images(filename1: String, filename2: String, patternGenerator: (Int, Int, Int, Int) -> BufferedImage,
                   primaryColor: Int= white, secondaryColor: Int = black) {
        val image1 = ImageIO.read(File(filename1)) ?: throw IllegalArgumentException("Wrong filename $filename1")
        val image2 = ImageIO.read(File(filename2)) ?: throw IllegalArgumentException("Wrong filename $filename2")


        val xRes = min(image1.width, image2.width)
        val yRes = min(image1.height, image2.height)

        val mask = patternGenerator(xRes, yRes, primaryColor, secondaryColor)
        val finalImage = BufferedImage(xRes, yRes, BufferedImage.TYPE_INT_RGB)

        var color1: Int
        var color2: Int
        var factor: Double
        var finalColor: Int
        for (i in 0 until yRes) {
            for (j in 0 until  xRes) {
                color1 = image1.getRGB(j, i)
                color2 = image2.getRGB(j, i)
                factor = normalizeRGB(mask.getRGB(j, i))


                finalColor = (color1 * factor + color2 * (1 - factor)).toInt()
                finalImage.setRGB(j, i, finalColor)
            }
        }

        val name = ("$filename1-$filename2").replace('.', '_')

        saveBitmap(finalImage, name)
    }

    private fun drawColumn(image: BufferedImage, x: Int, width: Int, color: Int) {
        for (i in 0 until image.height) {
            for (j in x..x + width) {
                if (j in 0 until image.width && i in 0 until image.height)
                    image.setRGB(j, i, color)
            }
        }
    }

    private fun drawRow(image: BufferedImage, y: Int, height: Int, color: Int) {
        for (i in 0 until image.width) {
            for (j in y..y + height) {
                if (i in 0 until image.width && j in 0 until image.height)
                    image.setRGB(i, j, color)
            }
        }
    }

    private fun drawCircle(image: BufferedImage, centerX: Int, centerY: Int, radius: Int, color: Int = black) {
        for (i in centerY - radius .. centerY + radius) {
            for(j in centerX - radius .. centerX + radius) {
                if (i in 0 until image.height && j in 0 until image.width) {
                    if (countDistance(j, i, centerX, centerY) <= radius)
                        image.setRGB(j, i, color)
                }
            }
        }
    }

    private fun drawConcentricCircle
                (image: BufferedImage, centerX: Int, centerY: Int, radius: Int=20, ringWidth: Int = 5, primaryColor: Int = black, secondaryColor:Int= white) {

        var ringIndex: Int
        var distance: Double
        for (i in centerY - radius .. centerY + radius) {
            for(j in centerX - radius .. centerX + radius) {
                if (i in 0 until image.height && j in 0 until image.width) {
                    distance = countDistance(j, i, centerX, centerY)
                    if (distance <= radius) {
                        ringIndex = distance.toInt() / ringWidth

                        if (ringIndex % 2 == 0)
                            image.setRGB(j, i, secondaryColor)
                        else
                            image.setRGB(j, i, primaryColor)

                    }
                }
            }
        }
    }

    private fun saveBitmap(bitmap: BufferedImage, filename: String): Boolean {
        try {
            val f = File("$filename.bmp")
            ImageIO.write(bitmap, "bmp", f)
        } catch (e: IOException) {
            return false
        }

        return true
    }

    private fun intToRGB(red: Int, green: Int, blue: Int): Int {
        val r = red and 0x000000FF
        val g = green and 0x000000FF
        val b = blue and 0x000000FF

        return (r shl 16) + (g shl 8) + b
    }

    private fun normalizeRGB(rgb: Int): Double {
        val blue = rgb and 0xff;
        val green = (rgb and 0xff00) shr 8;
        val red = (rgb and 0xff0000) shr 16;

        return (blue + green + red) / (3 * 255.0)
    }
    private fun countDistance(x1: Int, y1: Int, x2: Int, y2: Int)
            = sqrt( ( (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) ).toDouble() )
}




