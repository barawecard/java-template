package edu.spbu.matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import java.lang.NumberFormatException;
public class SparseMatrix extends Matrix {

  double[] data;
  int[] cols;
  int[] rowIndex;

  public SparseMatrix(int h, int w) {
    this.h = h;
    this.w = w;
  }

  public SparseMatrix(String fileName) throws FileNotFoundException, NumberFormatException {
    Scanner scan = new Scanner(new File(fileName));
    ArrayList<Double> temp = new ArrayList<>();
    ArrayList<Integer> columnNumber = new ArrayList<>();
    ArrayList<Integer> ElementsInColumn = new ArrayList<>();
    ElementsInColumn.add(0);
    this.h = 0;
    this.w = 0;
    int z, kol, numstart = -1;
    while (scan.hasNextLine()) {
      numstart++;
      z = 0;
      kol = 0;
      this.h++;
      String[] array = scan.nextLine().split(" ");
      for (String s : array) {
        if (s.length() != 0) {
          double el = Double.parseDouble(s);
          if (el != 0) {
            kol++;
            temp.add(el);
            columnNumber.add(z);
          }
          z++;
        }
      }
      if (this.w == 0) {
        this.w = z;
      } else if (this.w != z) {
        throw new NumberFormatException();
      }
      ElementsInColumn.add(kol + ElementsInColumn.get(numstart));
    }
    this.data = new double[temp.size()];
    this.cols = new int[columnNumber.size()];
    for (int i = 0; i < temp.size(); i++) {
      this.data[i] = temp.get(i);
      this.cols[i] = columnNumber.get(i);
    }
    this.rowIndex = new int[ElementsInColumn.size()];
    for (int i = 0; i < ElementsInColumn.size(); i++) {
      this.rowIndex[i] = ElementsInColumn.get(i);
    }
  }

  public boolean SparseEquals(Object o) {
    SparseMatrix s = (SparseMatrix) o;
    if ((this.h != s.h) || (this.w != s.w)) return false;
    return (Arrays.equals(this.data, s.data)) && (Arrays.equals(this.cols, s.cols))
            && (Arrays.equals(this.rowIndex, s.rowIndex));
  }

  public boolean SparseDenseEquals(Object o) {
    DenseMatrix m = (DenseMatrix) o;
    if ((this.h != m.h) || (this.w != m.w)) return false;
    for (int i = 0; i < m.h; i++) {
      for (int j = 0; j < m.w; j++) {
        if (m.data[i][j] != this.getElement(i, j)) return false;
      }
    }
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof DenseMatrix) {
      return this.SparseDenseEquals(o);
    } else {
      return this.SparseEquals(o);
    }
  }

  @Override
  public Matrix transposition() {
    SparseMatrix start = new SparseMatrix(this.w, this.h);
    ArrayList<TreeMap<Integer, Double>> tempdata = new ArrayList<>();
    for (int i = 0; i < start.h; i++) {
      TreeMap<Integer, Double> add1 = new TreeMap<>();
      tempdata.add(add1);
    }
    int nextrow = 1;
    for (int i = 0; i < this.cols.length; i++) {
      if (i >= this.rowIndex[nextrow]) nextrow++;
      (tempdata.get(this.cols[i])).put(nextrow - 1, data[i]);
    }
    start.rowIndex = new int[start.h + 1];
    start.rowIndex[0] = 0;
    int siz = 0;
    for (int i = 0; i < start.h; i++) {
      siz += tempdata.get(i).size();
      start.rowIndex[i + 1] = siz;
    }
    start.data = new double[this.data.length];
    start.cols = new int[this.data.length];
    int obg = 0;
    for (int i = 0; i < start.h; i++) {
      for (Map.Entry<Integer, Double> entry : tempdata.get(i).entrySet()) {
        start.cols[obg] = entry.getKey();
        start.data[obg] = entry.getValue();
        obg++;
      }
    }
    return start;
  }

  public Matrix mulSD(Matrix o)  {
    SparseMatrix res = new SparseMatrix(this.h, o.w);
    ArrayList<Double> tempdata = new ArrayList<>();
    ArrayList<Integer> columnNumber = new ArrayList<>();
    ArrayList<Integer> ElementsInColumn = new ArrayList<>();
    ElementsInColumn.add(0);
    double tempel;
    int kol = 0;
    for (int i = 0; i < this.rowIndex.length-1; i++) {
      int start = this.rowIndex[i], fin = this.rowIndex[i + 1];
      for (int j = 0; j < o.w; j++) {
        tempel = 0;
        for (int k = start; k < fin; k++) {
          tempel += this.data[k] * o.getElement(this.cols[k], j);
        }
        if (tempel != 0) {
          tempdata.add(tempel);
          columnNumber.add(j);
          kol++;
        }
      }
      ElementsInColumn.add(kol);
    }
    res.data = new double[tempdata.size()];
    res.cols = new int[columnNumber.size()];
    for (int i = 0; i < tempdata.size(); i++) {
      res.data[i] = tempdata.get(i);
      res.cols[i] = columnNumber.get(i);
    }
    res.rowIndex = new int[ElementsInColumn.size()];
    for (int i = 0; i < ElementsInColumn.size(); i++) {
      res.rowIndex[i] = ElementsInColumn.get(i);
    }
    return res;
  }
  public Matrix mulSS(Matrix o) {
    SparseMatrix s=(SparseMatrix) o.transposition();
    SparseMatrix res=new SparseMatrix(this.h,s.h);
    ArrayList<Double> tempdata = new ArrayList<>();
    ArrayList<Integer> columnNumber = new ArrayList<>();
    ArrayList<Integer> ElementsInColumn = new ArrayList<>();
    ElementsInColumn.add(0);
    int it1,it2,kol;
    double tempel;
    kol=0;
    for (int i = 0; i < this.rowIndex.length-1; i++)
    {
      int start = this.rowIndex[i], fin = this.rowIndex[i + 1];
      for(int j=0;j<s.rowIndex.length-1;j++)
      {
        int sts = s.rowIndex[j], fns = s.rowIndex[j + 1];
        tempel=0;
        it1 = start;
        it2 = sts;
        while ((it1 < fin) && (it2 < fns)) {
          if(this.cols[it1]==s.cols[it2])
          {
            tempel+=this.data[it1]*s.data[it2];
            it1++;
            it2++;
          }
          else if (this.cols[it1]<s.cols[it2])
          {
            it1++;
          }
          else
          {
            it2++;
          }
        }
        if(tempel!=0)
        {
          kol++;
          tempdata.add(tempel);
          columnNumber.add(j);
        }
      }
      ElementsInColumn.add(kol);
    }
    res.data = new double[tempdata.size()];
    res.cols = new int[columnNumber.size()];
    for (int i = 0; i < tempdata.size(); i++) {
      res.data[i] = tempdata.get(i);
      res.cols[i] = columnNumber.get(i);
    }
    res.rowIndex = new int[ElementsInColumn.size()];
    for (int i = 0; i < ElementsInColumn.size(); i++) {
      res.rowIndex[i] = ElementsInColumn.get(i);
    }
    return res;
  }

  @Override
  public Matrix mul(Matrix o) throws WrongSizeException {
    if (this.w != o.h) {
      throw new WrongSizeException();
    }
    Matrix mulm;
    if (o instanceof DenseMatrix) mulm = this.mulSD(o);
    else
    {
      mulm=this.mulSS(o);
    }
    return mulm;
  }

  @Override
  public Matrix mthmul(Matrix o) throws WrongSizeException, InterruptedException {
    if (this.w != o.h) {
      throw new WrongSizeException();
    }
    Matrix mulm;
    if (o instanceof DenseMatrix) mulm = this.mulSD(o);
    else
    {
      mulm=this.mthmulSS(o);
    }
    return mulm;
  }
  public SparseMatrix mthmulSS(final Matrix o) throws InterruptedException {
    final SparseMatrix s2=(SparseMatrix) o.transposition();
    final SparseMatrix s1=this;
    final SparseMatrix res=new SparseMatrix(s1.h,s2.h);
    final ArrayList<Double> tempdata = new ArrayList<>();
    ArrayList<Double> data1 = new ArrayList<>();
    ArrayList<Double> data2 = new ArrayList<>();
    ArrayList<Double> data3 = new ArrayList<>();
    ArrayList<Double> data4 = new ArrayList<>();
    ArrayList<Integer> Column1 = new ArrayList<>();
    ArrayList<Integer> Column2 = new ArrayList<>();
    ArrayList<Integer> Column3 = new ArrayList<>();
    ArrayList<Integer> Column4 = new ArrayList<>();
    final ArrayList<Integer> columnNumber = new ArrayList<>();
    res.rowIndex = new int[s2.h+1];
    res.rowIndex[0]=0;
    class ParallelSparseMulMatrix implements Runnable
    {
      final ArrayList<Double> data;
      final ArrayList<Integer> Column;
      final int sr,fr;
      public  ParallelSparseMulMatrix(ArrayList<Double> data,ArrayList<Integer> Column, int sr, int fr)
      {
        this.data=data;
        this.Column=Column;
        this.sr=sr;
        this.fr=fr;
      }
      @Override
      public void run() {
        for (int i = sr; i < fr; i++) {
          res.rowIndex[i+1]=res.rowIndex[i];
          int start = s1.rowIndex[i], fin = s1.rowIndex[i + 1];
          double tempel;
          int it1, it2;
          for (int j = 0; j < s2.rowIndex.length - 1; j++) {
            int sts = s2.rowIndex[j], fns = s2.rowIndex[j + 1];
            tempel = 0;
            it1 = start;
            it2 = sts;
            while ((it1 < fin) && (it2 < fns)) {
              if (s1.cols[it1] == s2.cols[it2]) {
                tempel += s1.data[it1] * s2.data[it2];
                it1++;
                it2++;
              } else if (s1.cols[it1] < s2.cols[it2]) {
                it1++;
              } else {
                it2++;
              }
            }
            if (tempel != 0) {
              res.rowIndex[i+1]++;
              data.add(tempel);
              Column.add(j);
            }
          }
        }
      }
    }
    int f1,f2,f3,f4;
    f4=s1.rowIndex.length-1;
    f1=f4/4;
    f2=f4/2;
    f3=f2+f1;
    Thread th1,th2,th3,th4;
    th1=new Thread(new ParallelSparseMulMatrix(data1,Column1,0,f1));
    th2=new Thread(new ParallelSparseMulMatrix(data2,Column2,f1,f2));
    th3=new Thread(new ParallelSparseMulMatrix(data3,Column3,f2,f3));
    th4=new Thread(new ParallelSparseMulMatrix(data4,Column4,f3,f4));
    th1.start(); th2.start(); th3.start(); th4.start();
    th1.join(); th2.join(); th3.join(); th4.join();
    tempdata.addAll(data1);
    tempdata.addAll(data2);
    tempdata.addAll(data3);
    tempdata.addAll(data4);
    columnNumber.addAll(Column1);
    columnNumber.addAll(Column2);
    columnNumber.addAll(Column3);
    columnNumber.addAll(Column4);
    res.data = new double[tempdata.size()];
    res.cols = new int[columnNumber.size()];
    for (int i = 0; i < tempdata.size(); i++) {
      res.data[i] = tempdata.get(i);
      res.cols[i] = columnNumber.get(i);
    }
    return res;
  }


  @Override
  public String toString() {
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < h; i++) {
      int start = rowIndex[i], fin = rowIndex[i + 1];
      int tempnum = -1;
      for (int w = start; w < fin; w++) {
        while (cols[w] - tempnum > 1) {
          res.append("0.0 ");
          tempnum++;
        }
        res.append(data[w]).append(" ");
        tempnum++;
      }
      while (this.w - tempnum > 1) {
        res.append("0.0 ");
        tempnum++;
      }
      res.append("\n");
    }
    return res.toString();
  }

  @Override
  public double getElement(int i, int j) {
    int start = rowIndex[i], fin = rowIndex[i + 1];
    for (int w = start; w < fin; w++) {
      if (cols[w] > j) return 0;
      else if (cols[w] == j) return data[w];
    }
    return 0;
  }

}