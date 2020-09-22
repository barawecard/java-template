package edu.spbu.sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by artemaliev on 07/09/15.
 */
public class IntSort {
  private static int partition(int arr[], int low, int high)
  {
    int pivot = arr[high];
    int i = (low-1);
    for (int j=low; j<high; j++)
    {
      if (arr[j] < pivot)
      {
        i++;
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
      }
    }
    int temp = arr[i+1];
    arr[i+1] = arr[high];
    arr[high] = temp;

    return i+1;
  }
  static void qsort(int arr[], int low, int high)
  {
    if (low < high)
    {
      int pi = partition(arr, low, high);
      qsort(arr, low, pi-1);
      qsort(arr, pi+1, high);
    }
  }
  public static void sort (int array[]) {
    qsort(array, 0, array.length-1);
  }

  public static void sort (List<Integer> list) {
    Collections.sort(list);
  }
}
