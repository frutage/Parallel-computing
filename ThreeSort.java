import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class ThreeSort {
	
	public static void main(String[] args) {
		int[] array = new int[30000];
		int[] arr = new int[30000];
		String path = args[0];
		int choose = Integer.parseInt(args[1]);
		if (choose < 1 || choose > 6) {
			System.out.println("Your input choice is not valid!");
			return;
		}

		try {
			FileInputStream fin = new FileInputStream(path + "random.txt");
			InputStreamReader reader = new InputStreamReader(fin, "UTF-8");
			StringBuffer sb = new StringBuffer();
			while (reader.ready()) {
				sb.append((char) reader.read());
			}
			reader.close();
			String[] str = sb.toString().split(" ");
			for (int index = 0; index < 30000; index++) {
				arr[index] = Integer.parseInt(str[index]);
			}
			
			long timeAll = 0;
			for (int i = 0; i < 11; i++) {
				array = arr.clone();
			long starTime=System.nanoTime();
			
			if (choose == 1) {
				quickSort(array, 0, array.length - 1);
			} else if (choose == 2) {
				quickSortPar(array, 0, array.length - 1);
			} else if (choose == 3) {
				enumSort(array);
			} else if (choose == 4) {
				enumSortPar(array);
			} else if (choose == 5) {
				int[] tmp = new int[array.length];
				mergeSort(array, tmp, 0, array.length - 1);
			} else if (choose == 6) {
				int[] tmp = new int[array.length];
				mergeSortPar(array, tmp);
			}
			long endTime = System.nanoTime();
			if (i >= 1) {
				System.out.println(endTime - starTime + "ns");
				timeAll += endTime - starTime;
			}
			}
			System.out.println("The running time is " + timeAll/10 + "ns.");
			
			FileOutputStream fop = new FileOutputStream(path + "result.txt");
			OutputStreamWriter writer = new OutputStreamWriter(fop, "UTF-8");
			for (int elem : array) {
				writer.append(String.valueOf(elem));
				writer.append(" ");
			}
			writer.close();
			fop.close();
		} catch (IOException e) {
			System.out.print("Exception");
		}
	}
	
	// Quick Sort--Serial
	public static void quickSort(int[] arr, int begin, int end){
	    if (begin < end){
	        int pivot = partition(arr, begin, end);        //将数组分为两部分
	        quickSort(arr, begin, pivot - 1);                   //递归排序左子数组
	        quickSort(arr, pivot + 1, end);                  //递归排序右子数组
	    }
	}
	
	private static int partition(int[] arr, int low, int high){
	    int pivot = arr[low];     //枢轴记录
	    while (low < high){
	        while (low < high && arr[high] >= pivot) 
	        	--high;
	        arr[low] = arr[high];             //交换比枢轴小的记录到左端
	        while (low < high && arr[low] <= pivot) 
	        	++low;
	        arr[high] = arr[low];           //交换比枢轴小的记录到右端
	    }
	    //扫描完成，枢轴到位
	    arr[low] = pivot;
	    //返回的是枢轴的位置
	    return low;
	}
	
	// Quick Sort--Parallel
	public static void quickSortPar(int arr[], int begin, int end) {
		if (begin < end) {
	        ForkJoinPool pool = new ForkJoinPool();
	        pool.submit(new QuickSortPar(arr, begin, end));
	        pool.shutdown();
		}
//		if (begin < end) {
//			Future<Integer> ft = executor.submit(new QuickSortPar(arr, begin, end));
//			try {
//				int pivot = ft.get();
//				if (begin < pivot - 1) {
//					quickSortPar(arr, begin, pivot - 1);
//				}
//				if (pivot + 1 < end) {
//					quickSortPar(arr, pivot + 1, end);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
	}
	
	// Enumeration Sort--Serial
	public static void enumSort(int[] arr) {
		int[] b = new int[arr.length];
		for (int i = 0; i < arr.length; i++) b[i] = 50001;
		for (int i = 0; i < arr.length; i++) {
			int pos = 0;
			for (int e : arr) {
				if (e < arr[i]) {
					pos++;
				}
			}
			while(b[pos] != 50001) pos++;
			b[pos] = arr[i];
		}
		for (int i = 0; i < arr.length; i++) {
			arr[i] = b[i];
		}
	}
	
	// Enumeration Sort--Parallel
	public static void enumSortPar(int[] arr) {
		ExecutorService executor = Executors.newCachedThreadPool();
		ArrayList<Future<Integer>> results = new ArrayList<Future<Integer>>();
//		final CountDownLatch latch = new CountDownLatch(arr.length);
		int[] b = new int[arr.length];
		boolean[] flag = new boolean[arr.length];
		for (int i = 0; i < arr.length; i++) {
			results.add(executor.submit(new EnumSortPar(arr, i)));
		}
		
		for (int i = 0; i < arr.length; i++) {
			Future<Integer> f = results.get(i);
			try {
				int pos = f.get();
				while (flag[pos]) 
					pos++;
				b[pos] = arr[i];
				flag[pos] = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < arr.length; i++) {
			arr[i] = b[i];
		}
		executor.shutdown();
	}
	
	// Merge Sort--Serial
	public static void mergeSort(int[] arr, int[] tmp, int first, int end) {
		if (first < end) {
			int mid = (first + end) / 2;
			mergeSort(arr, tmp, first, mid);
			mergeSort(arr, tmp, mid+1, end);
			merge(arr, tmp, first, mid, end);
		}
		
	}
	
	private static void merge(int[] arr, int[] tmp, int first, int mid, int end) {
		int i = first, j = mid + 1, k = first;
		while (i <= mid && j <= end) {
			if (arr[i] < arr[j])
				tmp[k++] = arr[i++];
			else
				tmp[k++] = arr[j++];
		}
		while (i <= mid)
			tmp[k++] = arr[i++];
		while (j <= end)
			tmp[k++] = arr[j++];
		for (i = first; i <= end; i++)
			arr[i] = tmp[i];
	}
	
	// Merge Sort--Parallel
	public static void mergeSortPar(int[] arr, int[] tmp) {
		CountDownLatch latch =  new CountDownLatch(2);
		new Thread(new Runnable() {
			@Override
			public void run() {
				ThreeSort.mergeSort(arr, tmp, 0, arr.length/2);
				latch.countDown();
			}
		}).start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				ThreeSort.mergeSort(arr, tmp, arr.length/2 + 1, arr.length - 1);
				latch.countDown();
			}
		}).start();
		
		try {
			latch.await();
		} catch (Exception e) {
			e.printStackTrace();
		}	
		merge(arr, tmp, 0, arr.length/2, arr.length - 1);
	}
}