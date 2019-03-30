import java.util.concurrent.Callable;

public class EnumSortPar implements Callable<Integer> {
	private int pos;
	private int[] array;
//	private CountDownLatch latch;
	
	EnumSortPar(int[] arr, int pos) {
		this.array = arr;
		this.pos = pos;
	}
	
	public Integer call() {
		int position = 0;
		for (int elem : array) {
			if (elem < array[pos]) 
				position++;
		}
		return position;
	}
}

