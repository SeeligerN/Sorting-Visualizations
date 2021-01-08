package sorting;

public class TimedAlgorithms implements Runnable {

	int[] toSort;
	SortingMain sm;
	SortingLabel sl;
	String speci;
	int resolution;
	int max;

	TimedAlgorithms(SortingLabel sl, String speci, SortingMain sortingMain, int resolution,
			int distance) {
		this.sl = sl;
		this.sm = sortingMain;
		this.speci = speci;
		this.resolution = resolution;
		this.max = distance;
	}

	@Override
	public void run() {
		int[] times = new int[resolution];

		int largestTime = 0;
		for (int i = 0; i < times.length; i++) {
			sm.setSorted();
			toSort = sm.setSize((i + 1) * (max / resolution));
			
			long timeStarted = System.currentTimeMillis();
			if (speci.equalsIgnoreCase("countingSort"))
				toSort = countingSort(toSort, sm.elements);
			else if (speci.equalsIgnoreCase("bubbleSort"))
				toSort = bubbleSort(toSort);
			else if (speci.equalsIgnoreCase("quicksort"))
				toSort = quickSort(toSort);
			else if (speci.equalsIgnoreCase("insertionsort"))
				toSort = insertionSort(toSort);
			else if (speci.equalsIgnoreCase("mergesort"))
				toSort = mergeSort(toSort, 0, toSort.length - 1);
			else if (speci.equalsIgnoreCase("selectionsort"))
				toSort = selectionSort(toSort);
			else if (speci.equalsIgnoreCase("shellsort"))
				toSort = shellSort(toSort);
			else if (speci.equalsIgnoreCase("heapsort"))
				toSort = heapSort(toSort);
			
			times[i] = (int) (System.currentTimeMillis() - timeStarted);
			
			System.out.println(speci + " sorted " + toSort.length + " elements in " + times[i] + " ms");
			
			largestTime = largestTime > times[i] ? largestTime : times[i];
			
			sl.visualize(times, largestTime);
		}
		
		sl.visualize(times, getLargestValueOf(times));
		
		sm.setAbortable(false);
	}

	public int[] bubbleSort(int[] toSort) {
		int n = toSort.length;
		int temp = 0;

		int[] sorted = new int[toSort.length];
		for (int i = 0; i < toSort.length; i++)
			sorted[i] = toSort[i];

		for (int i = 0; i < n; i++) {

			for (int j = 1; j < (n - i); j++) {
				if (sorted[j - 1] > sorted[j]) {
					// swap elements
					temp = sorted[j - 1];
					sorted[j - 1] = sorted[j];
					sorted[j] = temp;
				}
			}
		}
		return sorted;
	}

	// quickSort

	public int[] quickSort(int[] toSort) {
		if (toSort.length < 1)
			return toSort;

		int pivot = toSort[0];

		// sorting numbers that are bigger than the pivot
		int[] bigger = new int[0];
		for (int i = 0 + 1; i < toSort.length; i++)
			if (toSort[i] > pivot)
				bigger = append(bigger, toSort[i]);

		// sorting numbers that are smaller (or equal) than the pivot
		int[] smaller = new int[0];
		for (int i = 0 + 1; i < toSort.length; i++)
			if (toSort[i] <= pivot)
				smaller = append(smaller, toSort[i]);

		bigger = quickSort(bigger);
		smaller = quickSort(smaller);
		
		int[] sorted = new int[toSort.length];
		
		for (int i = 0; i < smaller.length; i++)
			sorted[i] = smaller[i];
		
		sorted[smaller.length] = pivot;
		
		for (int i = 0; i < bigger.length; i++)
			sorted[smaller.length + 1 + i] = bigger[i];

		return sorted;
	}

	public int[] append(int[] toAppendTo, int toAppend) {
		int[] appended = new int[toAppendTo.length + 1];
		for (int i = 0; i < toAppendTo.length; i++) {
			appended[i] = toAppendTo[i];
		}
		appended[appended.length - 1] = toAppend;
		return appended;
	}

	// countingSort

	public int[] countingSort(int[] toSort, int elements) {
		int[] indexes = new int[elements];
		
		for (int i = 0; i < toSort.length; i++) 
			indexes[toSort[i]]++;

		for (int i = 1; i < indexes.length; i++)
			indexes[i] += indexes[i - 1];

		for (int i = indexes.length - 1; i > 0; i--)
			indexes[i] = indexes[i - 1];

		indexes[0] = 0;

		int[] sorted = new int[toSort.length];
		for (int i = 0; i < toSort.length; i++) {
			int sortInAt = indexes[toSort[i]];
			sorted[sortInAt] = toSort[i];
			indexes[toSort[i]]++;
		}
		return sorted;
	}

	// insertionSort
	
	public int[] insertionSort(int[] toSort) {
		MAIN: for (int sortedEnd = 0; sortedEnd < toSort.length - 1; sortedEnd++) {
			int toInsert = toSort[sortedEnd + 1];

			for (int i = sortedEnd; i >= 0; i--) {

				if (toSort[i] > toInsert) {
					toSort[i + 1] = toSort[i];
				} else {
					toSort[i + 1] = toInsert;
					continue MAIN;
				}
			}
			toSort[0] = toInsert;
		}
		return toSort;
	}

	public int[] mergeSort(int[] toSort, int from, int to) {

		int length = to - from + 1;
		if (length <= 1)
			return toSort;

		int lastInAr1 = (from + ((to - from + 1) / 2)) - 1;

		toSort = mergeSort(toSort, from, lastInAr1);
		toSort = mergeSort(toSort, lastInAr1 + 1, to);

		merge(toSort, from, lastInAr1, to);

		return toSort;
	}

	public int[] merge(int[] whereToMerge, int start, int lastInFirst, int last) {
		int index1 = start;
		int index2 = lastInFirst + 1;

		int[] combined = new int[(last - start) + 1];
		for (int i = 0; i < combined.length; i++) {
			if (index1 > lastInFirst) {
				combined[i] = whereToMerge[index2];
				index2++;
				continue;
			}
			if (index2 > last) {
				combined[i] = whereToMerge[index1];
				index1++;
				continue;
			}

			if (whereToMerge[index1] < whereToMerge[index2]) {
				combined[i] = whereToMerge[index1];
				index1++;
			} else {
				combined[i] = whereToMerge[index2];
				index2++;
			}

		}

		for (int i = 0; i < combined.length; i++) {
			whereToMerge[start + i] = combined[i];
		}

		return whereToMerge;
	}

	// selection sort

	public int[] selectionSort(int[] toSort) {

		for (int firstUnsorted = 0; firstUnsorted < toSort.length; firstUnsorted++) {
			int smallestUnsortedIndex = firstUnsorted;
			for (int i = firstUnsorted; i < toSort.length; i++) {
				if (toSort[i] < toSort[smallestUnsortedIndex])
					smallestUnsortedIndex = i;
			}
			int smallestUnsorted = toSort[smallestUnsortedIndex];
			toSort[smallestUnsortedIndex] = toSort[firstUnsorted];
			toSort[firstUnsorted] = smallestUnsorted;
		}

		return toSort;
	}

	// shell sort ("improves" insertionSort)

	public int[] shellSort(int[] toSort) {

		for (int difference = toSort.length / 2; difference > 0; difference /= 2) {

			for (int i = difference; i < toSort.length; i++) {

				for (int onI = i; onI >= difference; onI -= difference) {
					if (toSort[onI - difference] > toSort[onI]) {
						int lower = toSort[onI - difference];
						toSort[onI - difference] = toSort[onI];
						toSort[onI] = lower;

					} else
						break;
				}
			}
		}

		return toSort;
	}

	// heap sort

	public int[] heapSort(int[] toSort) {
		while (!isHeapValid(toSort, toSort.length - 1))
			buildHeap(toSort, toSort.length - 1, 0);

		for (int lastInHeap = toSort.length - 1; lastInHeap > 0; lastInHeap--) {
			descendInHeap(toSort, lastInHeap, 0);

			int greatestValue = toSort[0];
			toSort[0] = toSort[lastInHeap];
			toSort[lastInHeap] = greatestValue;
		}
		
		return toSort;
	}

	public void buildHeap(int[] toHeapify, int lastInHeap, int on) {
		/*
		 * parent is at on; child L is at 2 * (on + 1) - 1; child R is at 2 * (on + 1);
		 */

		if (on > lastInHeap)
			return;

		// putting the greatest node into the top slot
		int leftChild = 2 * (on + 1) - 1;
		int rightChild = 2 * (on + 1);

		buildHeap(toHeapify, lastInHeap, rightChild);
		buildHeap(toHeapify, lastInHeap, leftChild);

		int greatestIndex;
		if (leftChild > lastInHeap)
			greatestIndex = on;
		else
			greatestIndex = toHeapify[on] > toHeapify[leftChild] ? on : leftChild;
		if (rightChild <= lastInHeap)
			greatestIndex = toHeapify[greatestIndex] > toHeapify[rightChild] ? greatestIndex : rightChild;

		// swapping greatest element into the root
		int buffer = toHeapify[on];
		toHeapify[on] = toHeapify[greatestIndex];
		toHeapify[greatestIndex] = buffer;
	}

	public void descendInHeap(int[] toDescendIn, int lastInHeap, int on) {
		/*
		 * parent is at on; child L is at 2 * (on + 1) - 1; child R is at 2 * (on + 1);
		 */

		if (on > lastInHeap)
			return;

		// putting the greatest node into the top slot
		int leftChild = 2 * (on + 1) - 1;
		int rightChild = 2 * (on + 1);

		int greatestIndex = on;
		if (leftChild <= lastInHeap)
			greatestIndex = toDescendIn[on] > toDescendIn[leftChild] ? on : leftChild;
		if (rightChild <= lastInHeap)
			greatestIndex = toDescendIn[greatestIndex] > toDescendIn[rightChild] ? greatestIndex : rightChild;

		// swapping greatest element into the root
		int buffer = toDescendIn[on];
		toDescendIn[on] = toDescendIn[greatestIndex];
		toDescendIn[greatestIndex] = buffer;

		// descending the root in the part its been switched into unless it hasnt been
		// descended
		
		if (greatestIndex != on)
			descendInHeap(toDescendIn, lastInHeap, greatestIndex);
	}
	
	public boolean isHeapValid(int[] toCheck, int lastInHeap) {
		for (int i = 0; i < lastInHeap; i++) {
			
			int leftChild = 2 * (i + 1) - 1;
			int rightChild = 2 * (i + 1);

			if (leftChild <= lastInHeap)
				if (toCheck[i] < toCheck[leftChild]) {
					return false;
				}
			if (rightChild <= lastInHeap)
				if (toCheck[i] < toCheck[rightChild]) {
					return false;
				}
		}
		return true;
	}
	
	// other
	
	public int getLargestValueOf(int[] toCheckIn) {
		int largestValue = 0;
		
		for (int i : toCheckIn)
			if (i > largestValue)
				largestValue = i;
		
		return largestValue;
	}
}
