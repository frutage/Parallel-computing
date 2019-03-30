import java.util.concurrent.RecursiveAction;

public class QuickSortPar extends RecursiveAction {
	
	private static final long serialVersionUID = 1L;
	private int[] arr;
	private int low;
	private int high;

	public QuickSortPar(int[] arr, int low, int high) {
	this.arr = arr;
	this.low = low;
	this.high = high;
}
	
	@Override
	protected void compute() {
		if(low < high) {
			int pivot = arr[low];
			int begin = low;
			int end = high;     //枢轴记录
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
//			System.out.println(Thread.currentThread().getName() + " " + low);
			QuickSortPar sortLeft = new QuickSortPar(arr, begin, low -1 );
			QuickSortPar sortRight = new QuickSortPar(arr, low + 1, end);
			sortLeft.fork();
			sortRight.fork();
//		} else
//			return;
		} else {
			if (low + 1 == high) {
				if( arr[low] > arr[high]) {
					int tmp = arr[low];
					arr[low] = arr[high];
					arr[high] = tmp;
				}
			} else if (low + 2 == high) {
				int min = (arr[low] < arr[low + 1]) ? arr[low] : arr[low+1];
				int max = arr[low] + arr[low + 1] - min;
				if (min <= arr[high]) {
					arr[low] = min;
					if (max > arr[high]) {
						arr[low + 1] = arr[high];
						arr[high] = max;
					}
				} else {
					arr[low] = arr[high];
					arr[low + 1] = min;
					arr[high] = max;
				}
				
			}
		}
	}
}

