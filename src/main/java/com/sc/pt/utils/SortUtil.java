package com.sc.pt.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: 1518008
 * Date: 12/15/15
 * Time: 4:22 PM
 * This code is just for internal use only.
 * If you need more info please contact the security admin.
 */
public class SortUtil {

    public static final int CUTOFF = 11;

    //获取ArrayList中的最大值

    public double ArrayListMax(ArrayList sampleList) throws Exception {
        try
        {
            double maxDevation = 0.0;
            int totalCount = sampleList.size();
            if (totalCount >= 1)
            {
                double max = Double.parseDouble(sampleList.get(0).toString());
                for (int i = 0; i < totalCount; i++)
                {
                    double temp = Double.parseDouble(sampleList.get(i).toString());
                    if (temp > max)
                    {
                        max = temp;
                    }
                } maxDevation = max;
            }
            return maxDevation;
        }
        catch (Exception ex)
        {
            throw ex;
        }

    }



//获取ArrayList中的最小值

    public double ArrayListMin(ArrayList sampleList) throws Exception {
        try
        {
            double mixDevation = 0.0;
            int totalCount = sampleList.size();
            if (totalCount >= 1)
            {
                double min = Double.parseDouble(sampleList.get(0).toString());
                for (int i = 0; i < totalCount; i++)
                {
                    double temp = Double.parseDouble(sampleList.get(i).toString());
                    if (min > temp)
                    {
                        min = temp;
                    }
                } mixDevation = min;
            }
            return mixDevation;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    public static void insertionSort(int[] data) {
        for (int index = 1; index < data.length; index++) {
            int key = data[index];
            int position = index;
            // shift larger values to the right
            while (position > 0 && data[position - 1] > key) {
                data[position] = data[position - 1];
                position--;
            }
            data[position] = key;
        }
    }

    public static <E extends Comparable<? super E>> void shellSort(List<E> a) {
        int h = 1;
        while (h < a.size()/3) h = h*3 + 1;    // <O(n^(3/2)) by Knuth,1973>: 1, 4, 13, 40, 121, ...
        for (; h >= 1; h /= 3)
            for (int i = h; i < a.size(); i++)
                for (int j = i; j >= h && a.get(j).compareTo(a.get(j-h)) < 0; j-=h)
                    Collections.swap(a, j, j - h);
    }

    public static void bubbleSort(int[] data) {
        int temp = 0;
        for (int i = data.length - 1; i > 0; --i) {
            boolean isSort = false;
            for (int j = 0; j < i; ++j) {
                if (data[j + 1] < data[j]) {
                    temp = data[j];
                    data[j] = data[j + 1];
                    data[j + 1] = temp;
                    isSort = true;
                }
            }
            // 如果一次内循环中发生了交换，那么继续比较；如果一次内循环中没发生任何交换，则认为已经排序好了。
            if (!isSort)
                break;
        }
    }

    public static void selectSort(int[] data) {
        int minIndex = 0;
        int temp = 0;
        for (int i = 0; i < data.length; i++) {
            minIndex = i; // 无序区的最小数据数组下标
            for (int j = i + 1; j < data.length; j++) { // 在无序区中找到最小数据并保存其数组下标
                if (data[j] < data[minIndex]) {
                    minIndex = j;
                }
            }
            if (minIndex != i) { // 如果不是无序区的最小值位置不是默认的第一个数据，则交换之。
                temp = data[i];
                data[i] = data[minIndex];
                data[minIndex] = temp;
            }
        }
    }

    public static int[] mergeSort(int[] arr) {// 归并排序 --递归
        if (arr.length == 1) {
            return arr;
        }
        int half = arr.length / 2;
        int[] arr1 = new int[half];
        int[] arr2 = new int[arr.length - half];
        System.arraycopy(arr, 0, arr1, 0, arr1.length);
        System.arraycopy(arr, half, arr2, 0, arr2.length);
        arr1 = mergeSort(arr1);
        arr2 = mergeSort(arr2);
        return mergeSortSub(arr1, arr2);
    }
    private static int[] mergeSortSub(int[] arr1, int[] arr2) {// 归并排序子程序
        int[] result = new int[arr1.length + arr2.length];
        int i = 0;
        int j = 0;
        int k = 0;
        while (true) {
            if (arr1[i] < arr2[j]) {
                result[k] = arr1[i];
                if (++i > arr1.length - 1) {
                    break;
                }
            } else {
                result[k] = arr2[j];
                if (++j > arr2.length - 1) {
                    break;
                }
            }
            k++;
        }
        for (; i < arr1.length; i++) {
            result[++k] = arr1[i];
        }
        for (; j < arr2.length; j++) {
            result[++k] = arr2[j];
        }
        return result;
    }

    /**
     * quick sort algorithm. <br />
     *
     * @param arr an array of Comparable items. <br />
     */
    public static <T extends Comparable<? super T>> void quicksort(T[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }
    /**
     * get the median of the left, center and right. <br />
     * order these and hide the pivot by put it the end of of the array. <br />
     *
     * @param arr an array of Comparable items. <br />
     * @param left the most-left index of the subarray. <br />
     * @param right the most-right index of the subarray.<br />
     * @return T
     */
    public static <T extends Comparable<? super T>> T median(T[] arr, int left, int right) {
        int center = (left + right) / 2;
        if (arr[left].compareTo(arr[center]) > 0)
            swapRef(arr, left, center);
        if (arr[left].compareTo(arr[right]) > 0)
            swapRef(arr, left, right);
        if (arr[center].compareTo(arr[right]) > 0)
            swapRef(arr, center, right);
        swapRef(arr, center, right - 1);
        return arr[right - 1];
    }
    /**
     * internal method to sort the array with quick sort algorithm. <br />
     *
     * @param arr an array of Comparable Items. <br />
     * @param left the left-most index of the subarray. <br />
     * @param right the right-most index of the subarray. <br />
     */
    private static <T extends Comparable<? super T>> void quickSort(T[] arr, int left, int right) {
        if (left + CUTOFF <= right) {
            // find the pivot
            T pivot = median(arr, left, right);
            // start partitioning
            int i = left, j = right - 1;
            for (;;) {
                while (arr[++i].compareTo(pivot) < 0);
                while (arr[--j].compareTo(pivot) > 0);
                if (i < j)
                    swapRef(arr, i, j);
                else
                    break;
            }
            // swap the pivot reference back to the small collection.
            swapRef(arr, i, right - 1);
            quickSort(arr, left, i - 1); // sort the small collection.
            quickSort(arr, i + 1, right); // sort the large collection.
        } else {
            // if the total number is less than CUTOFF we use insertion sort
            // instead (cause it much more efficient).
            insertionSort(arr, left, right);
        }
    }
    /**
     * method to swap references in an array.<br />
     *
     * @param arr an array of Objects. <br />
     * @param idx1 the index of the first element. <br />
     * @param idx2 the index of the second element. <br />
     */
    public static <T> void swapRef(T[] arr, int idx1, int idx2) {
        T tmp = arr[idx1];
        arr[idx1] = arr[idx2];
        arr[idx2] = tmp;
    }
    /**
     * method to sort an subarray from start to end with insertion sort
     * algorithm. <br />
     *
     * @param arr an array of Comparable items. <br />
     * @param start the begining position. <br />
     * @param end the end position. <br />
     */
    public static <T extends Comparable<? super T>> void insertionSort(T[] arr, int start, int end) {
        int i;
        for (int j = start + 1; j <= end; j++) {
            T tmp = arr[j];
            for (i = j; i > start && tmp.compareTo(arr[i - 1]) < 0; i--) {
                arr[i] = arr[i - 1];
            }
            arr[i] = tmp;
        }
    }


}
