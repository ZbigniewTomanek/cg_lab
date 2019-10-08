package lab1

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.math.sqrt


class BitmapGenerator {
    companion object {
        private val ringWidth = 10
        private val blurFactor = 2

        private val black = intToRGB(0, 0, 0)
        private val white = intToRGB(255, 255, 255)

        fun generateCircle(xRes: Int, yRes: Int): BufferedImage {
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
                        image.setRGB(j, i, black)
                    else
                        image.setRGB(j, i, white)
                }
            }

            saveBitmap(image, "circle")

            return image
        }

        fun generateFadingCircle(xRes: Int, yRes: Int): BufferedImage {
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

            val checkWidth = 12

            var isColumn: Boolean
            var isRow: Boolean
            for (i in 0 until yRes) {
                for (j in 0 until xRes) {
                    isColumn = (j / checkWidth) % 2 == 1
                    isRow = (i / checkWidth) % 2 == 1

                    if (isColumn || isRow) {
                        image.setRGB(j, i, secondaryColor)
                    } else {
                        image.setRGB(j, i, primaryColor)
                    }
                }
            }

            saveBitmap(image, "cheek")

            return image
        }

        fun generateBrettPattern(xRes: Int, yRes: Int, primaryColor: Int=black, secondaryColor: Int=white): BufferedImage {
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
                                image.setRGB(jj, ii, secondaryColor)
                            } else {
                                image.setRGB(jj, ii, primaryColor)
                            }
                        }
                    }

                    wasBlack = !wasBlack
                }
            }

            saveBitmap(image, "brett")

            return image
        }

        fun addPatternOnImage(filename: String, generatePattern: (Int, Int) -> BufferedImage): BufferedImage {
            val image = ImageIO.read(File(filename)) ?: throw IllegalArgumentException("Wrong filename")
            val patternBitmap = generatePattern(image.width, image.height)

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

        fun generateManySmallCircles
                    (xRes: Int, yRes: Int, circleRadius:Int=20,
                     circlesDistance:Int=10, primaryColor: Int= white, secondaryColor: Int= black): BufferedImage {
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
                    (xRes: Int, yRes: Int, circleRadius:Int=20, ringWidth: Int=5, primaryColor: Int= white, secondaryColor: Int= black): BufferedImage {
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
                    (image: BufferedImage, centerX: Int, centerY: Int, radius: Int, ringWidth: Int = 5, primaryColor: Int = black, secondaryColor:Int= white) {

            var ringIndex: Int
            var distance: Double
            for (i in centerY - radius .. centerY + radius) {
                for(j in centerX - radius .. centerX + radius) {
                    if (i in 0 until image.height && j in 0 until image.width) {
                        distance = countDistance(j, i, centerX, centerY)
                        if (distance <= radius) {
                            ringIndex = distance.toInt() / Companion.ringWidth

                            if (ringIndex % 2 == 0)
                                image.setRGB(j, i, secondaryColor)
                            else
                                image.setRGB(j, i, primaryColor)

                        }
                    }
                }
            }
        }

        fun saveBitmap(bitmap: BufferedImage, filename: String): Boolean {
            try {
                val f = File("$filename.bmp")
                ImageIO.write(bitmap, "bmp", f)
            } catch (e: IOException) {
                return false
            }

            return true
        }

        fun intToRGB(red: Int, green: Int, blue: Int): Int {
            val r = red and 0x000000FF
            val g = green and 0x000000FF
            val b = blue and 0x000000FF

            return (r shl 16) + (g shl 8) + b
        }

        fun countDistance(x1: Int, y1: Int, x2: Int, y2: Int)
                = sqrt( ( (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) ).toDouble() )
    }
}



