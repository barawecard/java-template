package edu.spbu.matrix;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * Плотная матрица
 */
public class DenseMatrix extends Matrix
{
  double data[][];

  public DenseMatrix(String fileName) throws FileNotFoundException,MemoryAllocateException,NumberFormatException {
  Scanner scan = new Scanner(new File(fileName));
  ArrayList <Double> temp = new ArrayList<>();
  this.w = 0;
  this.h = 0;
  int z = 0;
  while(scan.hasNextLine())
  {
    z = 0;
    this.h++;
    String[] temparr = scan.nextLine().split(" ");
    for (String s : temparr) {
      if (s.length() != 0)
      {
        double element = Double.parseDouble(s);
        if (!temp.add(element)) throw new MemoryAllocateException();
      }
      z++;
    }
  }

  if (this.w == 0) this.w = z;
  else if (this.w != z) throw new NumberFormatException();
  if ((this.h==0)||(this.w==0)) throw new NumberFormatException();
  this.data = new double[h][w];
  for (int i=0; i<temp.size(); i++)
    {
      this.data[i / w][i % w]=temp.get(i);
    }
  }

  @Override
  public double getElement(int i, int j)
  {
    return data[i][j];
  }
  public DenseMatrix(int w,int h)
  {
    this.w=w;
    this.h=h;
    this.data=new double[h][w];
  }

  @Override
  public String toString() {
    StringBuilder result= new StringBuilder();
    for(int i=0;i<h;i++)
    {
      for(int j=0;j<w;j++)
      {
        result.append(data[i][j]).append(" ");
      }
      if(i!=h-1) result.append("\n");
    }
    return result.toString();
  }

  @Override
  public Matrix transposition() {
    DenseMatrix m=new DenseMatrix(this.h,this.w);
    for(int i=0;i<this.h;i++)
    {
      for(int j=0;j<this.w;j++)
      {
        m.data[j][i]=this.data[i][j];
      }
    }
    return m;
  }

  @Override public Matrix mul(Matrix o) throws WrongSizeException {
    if (this.w != o.h) throw new WrongSizeException();
    Matrix res;
    if (o instanceof DenseMatrix) {
      res = this.mulDD((DenseMatrix) o);
    }
    else
    {
      SparseMatrix s = (SparseMatrix) o;
      DenseMatrix m = this;
      res=(s.transposition().mul(m.transposition())).transposition();
    }
    return res;
  }

  @Override
  public Matrix mthmul(Matrix o) throws WrongSizeException, InterruptedException {
    if (this.w != o.h) {
      throw new WrongSizeException();
    }
    Matrix mulm;
    if (o instanceof DenseMatrix) {
      mulm = this.mthmulDD((DenseMatrix) o);
    }
    else
    {
      SparseMatrix s=(SparseMatrix) o;
      DenseMatrix m=this;
      mulm=(s.transposition().mul(m.transposition())).transposition();
    }
    return mulm;
  }

  public DenseMatrix mthmulDD(final DenseMatrix o) throws InterruptedException {
    final DenseMatrix m=new DenseMatrix(o.w,this.h);
    final DenseMatrix m1=this;
    class SplitDenceMatrix implements Runnable
    {
      final int rs,rf,cs,cf;

      public  SplitDenceMatrix(int rs,int cs, int rf, int cf)
      {
        this.rs=rs;
        this.rf=rf;
        this.cs=cs;
        this.cf=cf;
      }

      @Override
      public void run() {
        for (int i = rs; i < rf; i++) {
          for (int j = cs; j < cf; j++) {
            m.data[i][j] = 0;
            for (int k = 0; k < m1.w; k++) {
              m.data[i][j] += (m1.data[i][k] * o.data[k][j]);
            }
          }
        }
      }
    }

    Thread thread1,thread2,thread3,thread4;
    int rmid=m.w/2, cmid=m.h/2;
    thread1=new Thread(new SplitDenceMatrix(0,0,rmid,cmid));
    thread2=new Thread(new SplitDenceMatrix(rmid,0,m.w,cmid));
    thread3=new Thread(new SplitDenceMatrix(0,cmid,rmid,m.h));
    thread4=new Thread(new SplitDenceMatrix(rmid,cmid,m.w,m.h));

    thread1.start();
    thread2.start();
    thread3.start();
    thread4.start();

    thread1.join();
    thread2.join();
    thread3.join();
    thread4.join();
    return m;
  }

  public DenseMatrix mulDD(DenseMatrix o)
  {
    DenseMatrix m=new DenseMatrix(o.w,this.h);
    for(int i=0;i<m.h;i++)
    {
      for(int j=0;j<m.w;j++)
      {
        m.data[i][j]=0;
        for(int k=0;k<o.h;k++)
        {
          m.data[i][j]+=this.data[i][k]*(o.getElement(k,j));
        }
      }
    }
    return m;
  }
  @Override public boolean equals(Object o)
  {
    if (o instanceof DenseMatrix)
    {
      return  this.ddequals(o);
    }
    else
    {
      return o.equals(this);
    }
  }
  public boolean ddequals(Object o) {
    if (getClass() != o.getClass()) return false;
    DenseMatrix m = (DenseMatrix) o;
    if ((this.h != m.h) || (this.w != m.w)) {
      return false;
    }
    for (int i = 0; i < this.h; i++) {
      for (int j = 0; j < this.w; j++) {
        if (this.data[i][j] != m.data[i][j]) {
          return false;
        }
      }
    }
    return true;
  }
}
