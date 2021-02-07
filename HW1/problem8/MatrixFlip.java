public class MatrixFlip {
    public static void printFlippedMatrix(char[][] matrix) {
        // DO NOT change the skeleton code.
        // You can add codes anywhere you want.
        int ncol = matrix[0].length;
        int nrow = matrix.length;
        for(int i = nrow-1;i>=0;i--){
            for(int j=ncol-1;j>=0;j--){
                System.out.print(matrix[i][j]);
            }
            System.out.println();
        }
    }
}