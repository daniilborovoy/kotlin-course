package lab_1

class Lab1 {
    companion object {
        fun run() {
            val matrixTool = MatrixTools()
            val matrix1 = matrixTool.enterMatrixFromFile("/home/daniil/IdeaProjects/study-kotlin/matrix.txt")
            //    val matrix1 = enterMatrix()
            println("You entered the following matrix 1:")
            matrixTool.printMatrix(matrix1)

            val matrix2 = matrixTool.enterMatrixFromFile("/home/daniil/IdeaProjects/study-kotlin/matrix.txt")
            println("You entered the following matrix 2:")
            matrixTool.printMatrix(matrix2)

            val result = matrixTool.multiplyMatrices(matrix1, matrix2)
            if (result != null) {
                println("Result of matrix multiplication:")
                matrixTool.printMatrix(result)
            } else {
                println("Matrix multiplication cannot be performed due to incompatible dimensions.")
            }
        }
    }
}
