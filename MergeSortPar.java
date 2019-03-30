import java.util.concurrent.RecursiveAction;

public class MergeSortPar extends RecursiveAction {
	
	private static final long serialVersionUID = 1L;
	private int[] arr;
	private int[] tmp;
	private int low;
	private int high;
	
	public MergeSortPar(int[] arr, int[] tmp, int low, int high) {
		this.arr = arr;
		this.tmp = tmp;
		this.low = low;
		this.high = high;
	}
	
	@Override
	protected void compute() {
		if (low + 1 < high) {
			int mid = (low + high) / 2;
			MergeSortPar sortLeft = new MergeSortPar(arr, tmp, low, mid);
			sortLeft.invoke();
			MergeSortPar sortRight = new MergeSortPar(arr, tmp, mid + 1, high);
			sortRight.invoke();
			int i = low, j = mid + 1, k = low;
			while (i <= mid && j <= high) {
				if (arr[i] < arr[j])
					tmp[k++] = arr[i++];
				else
					tmp[k++] = arr[j++];
			}
			while (i <= mid)
				tmp[k++] = arr[i++];
			while (j <= high)
				tmp[k++] = arr[j++];
			for (i = low; i <= high; i++)
				arr[i] = tmp[i];
		} else {
			if (low + 1 == high && arr[low] > arr[high]) {
				int tmp = arr[low];
				arr[low] = arr[high];
				arr[high] = tmp;
			}
		}
	}
}
