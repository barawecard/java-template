package edu.spbu.matrix;

import edu.spbu.matrix.DenseMatrix;
import edu.spbu.matrix.Matrix;
import java.io.FileNotFoundException;



public class MultiThreadingPerformanceTest {

    public static final String MATRIX1path = "m1.txt";
    public static final String MATRIX2path = "m2.txt";

    public static void main(String s[]) throws FileNotFoundException, MemoryAllocateException, WrongSizeException {

        System.out.println("Starting loading dense matrices");
        Matrix m1 = new DenseMatrix(MATRIX1path);
        System.out.println("1st OK");
        Matrix m2 = new DenseMatrix(MATRIX2path);
        System.out.println("2nd OK");
        long start = System.currentTimeMillis();
        Matrix res1 = m1.mul(m2);
        System.out.println("Dense Matrix time: " + (System.currentTimeMillis() - start));

        System.out.println("Starting loading dense and sparse matrices");
        m1 = new DenseMatrix(MATRIX1path);
        System.out.println("1st OK");
        m2 = new SparseMatrix(MATRIX2path);
        System.out.println("2nd OK");
        start = System.currentTimeMillis();
        Matrix res2 = m1.mul(m2);
        System.out.println("Dense Matrix time: " + (System.currentTimeMillis() - start));

        System.out.println("Starting loading sparse and dense matrices");
        m1 = new SparseMatrix(MATRIX1path);
        System.out.println("1 loaded");
        m2 = new DenseMatrix(MATRIX2path);
        System.out.println("2 loaded");
        start = System.currentTimeMillis();
        Matrix res3 = m1.mul(m2);
        System.out.println("Dense Matrix time: " + (System.currentTimeMillis() - start));

        System.out.println("Starting loading sparse matrices");
        m1 = new SparseMatrix(MATRIX1path);
        System.out.println("1 loaded");
        m2 = new SparseMatrix(MATRIX2path);
        System.out.println("2 loaded");
        start = System.currentTimeMillis();
        Matrix res4 = m1.mul(m2);

        System.out.println("Sparse Matrix time: " + (System.currentTimeMillis() - start));
        System.out.println("1st equals: " + (res1.equals(res2)) + " 2nd equals:  " + res1.equals(res3) + " 3rd equals: " + res1.equals(res4));
    }
}