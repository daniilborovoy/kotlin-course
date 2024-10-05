package lab_1

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import kotlin.system.exitProcess


class MatrixTools {
    fun enterMatrix(): Array<Array<Int>> {
        val rows = readNumberFromUserSafely("Enter the number of rows:")
        val cols = readNumberFromUserSafely("Enter the number of columns:")
        return readMatrixRowValuesFromUserSafely(rows, cols)
    }

    fun enterMatrixFromFile(input: String? = null): Array<Array<Int>> {
        println("Enter the file path:")
        val filePath = input ?: readln()

        val file = File(filePath)
        if (!file.exists()) {
            println("Error: Файл не найден.")
            exitProcess(1)
        }

        if (!file.canRead()) {
            println("Error: Файл не найден.")
            exitProcess(1)
        }

        try {
            val lines = file.readLines()
            if (lines.isEmpty()) {
                println("Error: Файл не найден.")
                return Array(0) { emptyArray() }
            }

            val matrix = Array(lines.size) { Array(lines[0].split(" ").size) { 0 } }

            for (i in lines.indices) {
                val input = lines[i].split(" ").filter { it.isNotBlank() } // Убираем пустые строки
                if (input.isEmpty()) {
                    println("Error: строка ${i + 1}.")
                    return Array(0) { emptyArray() }
                }

                try {
                    matrix[i] = input.map { it.toInt() }.toTypedArray()
                } catch (e: NumberFormatException) {
                    println("Invalid number format in line ${i + 1}.")
                    exitProcess(1)
                }
            }

            return matrix
        } catch (e: FileNotFoundException) {
            println("Error: File not found.")
            exitProcess(1)
        } catch (e: IOException) {
            println("Error: An IO error occurred while reading the file.")
            exitProcess(1)
        }
    }

    fun printMatrix(matrix: Array<Array<Int>>) {
        for (row in matrix) {
            println(row.joinToString(" "))
        }
    }

    private fun readNumberFromUserSafely(title: String): Int {
        while (true) {
            try {
                println(title)
                val number = readln().toInt()
                return number
            }   catch (e: Throwable) {
                println("Wrong input, please enter a valid number!!!!!")
            }
        }
    }

    private fun readMatrixRowValuesFromUserSafely(rows: Int, cols: Int): Array<Array<Int>> {
        val matrix = Array(rows) { Array(cols) { 0 } }

        for (i in 0..<rows) {
            while (true) {
                println("Enter values for row ${i + 1} (space-separated):")
                val input = readln().split(" ")

                try {
                    if (input.size == cols) {
                        matrix[i] = input.map { it.toInt() }.toTypedArray()
                        break
                    } else {
                        println("You must enter exactly $cols numbers. Please try again.")
                    }
                } catch (e: NumberFormatException) {
                    println("Invalid input. Please enter only integers.")
                }
            }
        }

        return matrix
    }

    fun multiplyMatrices(matrix1: Array<Array<Int>>, matrix2: Array<Array<Int>>): Array<Array<Int>>? {
        val rows1 = matrix1.size
        val cols1 = matrix1[0].size
        val rows2 = matrix2.size
        val cols2 = matrix2[0].size

        // Проверка совместимости для умножения
        if (cols1 != rows2) {
            return null
        }

        val result = Array(rows1) { Array(cols2) { 0 } }

        for (i in 0..<rows1) {
            for (j in 0..<cols2) {
                for (k in 0..<cols1) {
                    result[i][j] += matrix1[i][k] * matrix2[k][j]
                }
            }
        }

        return result
    }
}