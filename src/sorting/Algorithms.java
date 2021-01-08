package sorting;

public class Algorithms implements Runnable {

	int[] toSort;
	SortingMain sm;
	SortingLabel sl;
	String speci;
	long timeStarted;
	boolean multiComparisons;
	int max;
	int resolution;
	int comparisons;

	float toWait = 0;

	Algorithms(SortingLabel sl, String specification, SortingMain sortingMain, boolean multiComparisons, int max,
			int resolution) {
		this.sl = sl;
		this.sm = sortingMain;
		this.speci = specification;
		this.multiComparisons = multiComparisons;
		this.max = max;
		this.resolution = resolution;
		this.comparisons = 0;
	}

	@Override
	public void run() {
		if (multiComparisons) {
			int[] comparisonsA = new int[resolution];

			int mostComparisons = 0;
			for (int i = 0; i < comparisonsA.length; i++) {
				sm.setSorted();
				toSort = sm.setSize((i + 1) * (max / resolution));

				comparisons = 0;
				if (speci.equalsIgnoreCase("countingSort"))
					toSort = countingSort(toSort);
				else if (speci.equalsIgnoreCase("bubbleSort"))
					toSort = bubbleSort(toSort);
				else if (speci.equalsIgnoreCase("quicksort"))
					toSort = quickSort(toSort, 0, toSort.length - 1);
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

				comparisonsA[i] = comparisons;

				System.out.println(
						speci + " sorted " + toSort.length + " elements in " + comparisonsA[i] + " comparisons");

				mostComparisons = mostComparisons > comparisonsA[i] ? mostComparisons : comparisonsA[i];

				sl.visualize(comparisonsA, mostComparisons);
			}

			sl.visualize(comparisonsA, getLargestValueOf(comparisonsA));

			sm.setAbortable(false);
		} else {
			if (!sm.updateFields())
				return;

			toSort = sm.generateArray(sm.size);
			sl.visualize(toSort, sm.elements);

			timeStarted = System.currentTimeMillis();
			if (speci.equalsIgnoreCase("countingSort"))
				toSort = countingSort(toSort);
			else if (speci.equalsIgnoreCase("bubbleSort"))
				toSort = bubbleSort(toSort);
			else if (speci.equalsIgnoreCase("quicksort"))
				toSort = quickSort(toSort, 0, toSort.length - 1);
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

			sm.setTime(System.currentTimeMillis() - timeStarted);
			sm.setComparisons(comparisons);
			sl.visualize(toSort, sm.elements);
			sm.setAbortable(false);
			sm.setSorted();
		}
	}

	/**
	 * BUBBLE SORT
	 * 
	 * Bubblesorts the Array and Returns it.
	 * 
	 * Bubble sort, sometimes referred to as sinking sort, is a simple sorting
	 * algorithm that repeatedly steps through the list to be sorted, compares each
	 * pair of adjacent items and swaps them if they are in the wrong order. The
	 * pass through the list is repeated until no swaps are needed, which indicates
	 * that the list is sorted. The algorithm, which is a comparison sort, is named
	 * for the way smaller or larger elements "bubble" to the top of the list.
	 * Although the algorithm is simple, it is too slow and impractical for most
	 * problems even when compared to insertion sort. It can be practical if the
	 * input is usually in sorted order but may occasionally have some out-of-order
	 * elements nearly in position.
	 * 
	 * Wikipedia (https://en.wikipedia.org/wiki/Bubble_sort)
	 * 
	 * @param toSort
	 *            the array that is to be sorted.
	 * @return the sorted array.
	 */
	public int[] bubbleSort(int[] toSort) {
		int n = toSort.length;
		int temp = 0;

		int[] sorted = new int[toSort.length];
		for (int i = 0; i < toSort.length; i++)
			sorted[i] = toSort[i];

		for (int i = 0; i < n; i++) {

			for (int j = 1; j < (n - i); j++) {

				comparisons++;
				if (sorted[j - 1] > sorted[j]) {
					// swap elements
					temp = sorted[j - 1];
					sorted[j - 1] = sorted[j];
					sorted[j] = temp;
				}
				visualize(sorted);
			}
		}
		return sorted;
	}

	/**
	 * QUICK SORT
	 * 
	 * Quicksort (sometimes called partition-exchange sort) is an efficient sorting
	 * algorithm, serving as a systematic method for placing the elements of an
	 * array in order. Developed by Tony Hoare in 1959[1] and published in 1961,[2]
	 * it is still a commonly used algorithm for sorting. When implemented well, it
	 * can be about two or three times faster than its main competitors, merge sort
	 * and heapsort.
	 * 
	 * Wikipedia (https://en.wikipedia.org/wiki/Quicksort)
	 * 
	 * @param toSort
	 *            the Array that is to be sorted.
	 * @param from
	 *            the index it is to be sorted from.
	 * @param to
	 *            the index it is to be sorted to.
	 * @return the sorted Array.
	 */

	public int[] quickSort(int[] toSort, int from, int to) {
		if (to - from < 1)
			return toSort;

		int pivot = toSort[from];

		// sorting numbers that are bigger than the pivot
		// sorting numbers that are smaller (or equal) than the pivot
		int[] bigger = new int[0];
		int[] smaller = new int[0];
		for (int i = from + 1; i <= to; i++) {
			comparisons++;
			
			if (toSort[i] <= pivot)
				smaller = append(smaller, toSort[i]);
			else
				bigger = append(bigger, toSort[i]);
		}

		// putting them together
		int[] sorted = new int[toSort.length];
		for (int i = 0; i < toSort.length; i++)
			sorted[i] = toSort[i];

		for (int i = 0; i < smaller.length; i++)
			sorted[from + i] = smaller[i];

		sorted[from + smaller.length] = pivot;

		for (int i = 0; i < bigger.length; i++)
			sorted[from + smaller.length + 1 + i] = bigger[i];

		visualize(toSort);
		
		sorted = quickSort(sorted, from, from + smaller.length - 1);
		sorted = quickSort(sorted, from + smaller.length + 1, to);

		return sorted;
	}

	/**
	 * Method exclusively used by QuickSort to append an integer to an already
	 * existing integer Array.
	 * 
	 * @param toAppendTo
	 *            the Array the Value is to be appended to.
	 * @param toAppend
	 *            the integer that is to be appended.
	 * @return returns the new Array with the appended value at the top.
	 */
	public int[] append(int[] toAppendTo, int toAppend) {
		int[] appended = new int[toAppendTo.length + 1];
		for (int i = 0; i < toAppendTo.length; i++) {
			appended[i] = toAppendTo[i];
		}
		appended[appended.length - 1] = toAppend;
		return appended;
	}

	/**
	 * COUNTING SORT
	 * 
	 * counting sort is an algorithm for sorting a collection of objects according
	 * to keys that are small integers; that is, it is an integer sorting algorithm.
	 * It operates by counting the number of objects that have each distinct key
	 * value, and using arithmetic on those counts to determine the positions of
	 * each key value in the output sequence. Its running time is linear in the
	 * number of items and the difference between the maximum and minimum key
	 * values, so it is only suitable for direct use in situations where the
	 * variation in keys is not significantly greater than the number of items.
	 * However, it is often used as a subroutine in another sorting algorithm, radix
	 * sort, that can handle larger keys more efficiently.
	 * 
	 * Wikipedia (https://en.wikipedia.org/wiki/Counting_sort)
	 * 
	 * @param toSort
	 *            the Array that is to be sorted.
	 * @return the sorted array.
	 */

	public int[] countingSort(int[] toSort) {
		int[] indexes = new int[sm.elements];

		for (int i : toSort)
			indexes[i]++;

		for (int i = 1; i < indexes.length; i++)
			indexes[i] += indexes[i - 1];

		for (int i = indexes.length - 1; i > 0; i--)
			indexes[i] = indexes[i - 1];

		indexes[0] = 0;

		int[] sorted = new int[toSort.length];
		for (int i = 0; i < toSort.length; i++) {
			// debatable, because its technically not comparing
			comparisons++;
			sorted[indexes[toSort[i]]] = toSort[i];
			indexes[toSort[i]]++;

			visualize(sorted);
		}
		return sorted;
	}

	/**
	 * INSERTION SORT
	 * 
	 * Insertion sort iterates, consuming one input element each repetition, and
	 * growing a sorted output list. At each iteration, insertion sort removes one
	 * element from the input data, finds the location it belongs within the sorted
	 * list, and inserts it there. It repeats until no input elements remain.
	 * 
	 * Wikipedia (https://en.wikipedia.org/wiki/Insertion_sort)
	 * 
	 * @param toSort
	 *            the array that is to be sorted
	 * @return the sorted array.
	 */

	public int[] insertionSort(int[] toSort) {
		MAIN: for (int sortedEnd = 0; sortedEnd < toSort.length - 1; sortedEnd++) {
			int toInsert = toSort[sortedEnd + 1];

			for (int i = sortedEnd; i >= 0; i--) {

				comparisons++;
				if (toSort[i] > toInsert) {
					toSort[i + 1] = toSort[i];
				} else {
					toSort[i + 1] = toInsert;
					continue MAIN;
				}

				visualize(toSort);
			}
			toSort[0] = toInsert;
		}

		/*
		 * because of bug when sorting a sorted array with insertion sort waiting for
		 * 1ms seems to fix the issue
		 */
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
		}
		return toSort;
	}

	/**
	 * MERGE SORT
	 * 
	 * merge sort (also commonly spelled mergesort) is an efficient,
	 * general-purpose, comparison-based sorting algorithm. Most implementations
	 * produce a stable sort, which means that the implementation preserves the
	 * input order of equal elements in the sorted output. Mergesort is a divide and
	 * conquer algorithm that was invented by John von Neumann in 1945. A detailed
	 * description and analysis of bottom-up mergesort appeared in a report by
	 * Goldstine and von Neumann as early as 1948.
	 * 
	 * Wikipedia (https://en.wikipedia.org/wiki/Merge_sort)
	 * 
	 * @param toSort
	 *            the array that is to be sorted
	 * @param from
	 *            the index the array is to be sorted from.
	 * @param to
	 *            the index the array is to be sorted to.
	 * @return the sorted array.
	 */

	public int[] mergeSort(int[] toSort, int from, int to) {

		int length = to - from + 1;
		if (length <= 1)
			return toSort;

		int lastInAr1 = (from + ((to - from + 1) / 2)) - 1;

		toSort = mergeSort(toSort, from, lastInAr1);
		toSort = mergeSort(toSort, lastInAr1 + 1, to);

		merge(toSort, from, lastInAr1, to);

		visualize(toSort);

		return toSort;
	}

	/**
	 * A Method exclusively used by merge sort used to merge to adjacent subArrays
	 * in one array. The latter subArray is required to start directly after the
	 * first one ends.
	 * 
	 * @param whereToMerge
	 *            the Array the two subArrays are in.
	 * @param start
	 *            the first index of the first array.
	 * @param lastInFirst
	 *            the last index of the first array.
	 * @param last
	 *            the last index of the latter subArray.
	 * @return the new Array in which the two subArrays are merged.
	 */

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

			comparisons++;
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
			visualize(whereToMerge);
		}

		return whereToMerge;
	}

	/**
	 * SELECTION SORT
	 * 
	 * selection sort is a sorting algorithm, specifically an in-place comparison
	 * sort. It has O(n2) time complexity, making it inefficient on large lists, and
	 * generally performs worse than the similar insertion sort. Selection sort is
	 * noted for its simplicity, and it has performance advantages over more
	 * complicated algorithms in certain situations, particularly where auxiliary
	 * memory is limited.
	 *
	 * Wikipedia (https://en.wikipedia.org/wiki/Selection_sort)
	 * 
	 * @param toSort
	 *            the array that is to be sorted.
	 * @return the sorted array.
	 */

	public int[] selectionSort(int[] toSort) {

		for (int firstUnsorted = 0; firstUnsorted < toSort.length; firstUnsorted++) {
			int smallestUnsortedIndex = firstUnsorted;
			for (int i = firstUnsorted; i < toSort.length; i++) {
				comparisons++;
				if (toSort[i] < toSort[smallestUnsortedIndex])
					smallestUnsortedIndex = i;
			}
			int smallestUnsorted = toSort[smallestUnsortedIndex];
			toSort[smallestUnsortedIndex] = toSort[firstUnsorted];
			toSort[firstUnsorted] = smallestUnsorted;

			visualize(toSort);
		}

		return toSort;
	}

	/**
	 * SHELL SORT
	 * 
	 * Shellsort, also known as Shell sort or Shell's method, is an in-place
	 * comparison sort. It can be seen as either a generalization of sorting by
	 * exchange (bubble sort) or sorting by insertion (insertion sort). The method
	 * starts by sorting pairs of elements far apart from each other, then
	 * progressively reducing the gap between elements to be compared. Starting with
	 * far apart elements, it can move some out-of-place elements into position
	 * faster than a simple nearest neighbor exchange. Donald Shell published the
	 * first version of this sort in 1959. The running time of Shellsort is heavily
	 * dependent on the gap sequence it uses. For many practical variants,
	 * determining their time complexity remains an open problem.
	 * 
	 * Wikipedia (https://en.wikipedia.org/wiki/Shellsort)
	 * 
	 * @param toSort
	 *            the array that is to be sorted.
	 * @return the sorted array.
	 */

	public int[] shellSort(int[] toSort) {

		for (int difference = toSort.length / 2; difference > 0; difference /= 2) {

			for (int i = difference; i < toSort.length; i++) {

				for (int onI = i; onI >= difference; onI -= difference) {
					comparisons++;
					if (toSort[onI - difference] > toSort[onI]) {
						int lower = toSort[onI - difference];
						toSort[onI - difference] = toSort[onI];
						toSort[onI] = lower;

					} else
						break;
				}
				visualize(toSort);
			}

		}

		return toSort;
	}

	/**
	 * HEAP SORT
	 * 
	 * Heapsort is a comparison-based sorting algorithm. Heapsort can be thought of
	 * as an improved selection sort: like that algorithm, it divides its input into
	 * a sorted and an unsorted region, and it iteratively shrinks the unsorted
	 * region by extracting the largest element and moving that to the sorted
	 * region. The improvement consists of the use of a heap data structure rather
	 * than a linear-time search to find the maximum.
	 * 
	 * Wikipedia (https://en.wikipedia.org/wiki/Heapsort)
	 * 
	 * @param toSort
	 *            the array that is to be sorted
	 * @return the sorted array
	 */

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

	/**
	 * A Method exclusively used by heapsort to "heapify" a subArray.
	 * 
	 * @param toHeapify
	 *            the array containing the subArray that is to be heapified.
	 * @param lastInHeap
	 *            the index that indicates the last in the subArray.
	 * @param on
	 *            since this method is called recursively this value is used to
	 *            specify where the compiler "is".
	 */
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
		else {
			comparisons++;
			greatestIndex = toHeapify[on] > toHeapify[leftChild] ? on : leftChild;
		}
		if (rightChild <= lastInHeap) {
			comparisons++;
			greatestIndex = toHeapify[greatestIndex] > toHeapify[rightChild] ? greatestIndex : rightChild;
		}

		// swapping greatest element into the root
		int buffer = toHeapify[on];
		toHeapify[on] = toHeapify[greatestIndex];
		toHeapify[greatestIndex] = buffer;

		visualize(toHeapify);
	}

	/**
	 * A Method exclusively used by Heapsort to descend the root down in an already
	 * valid Heap to restore full validity.
	 * 
	 * @param toDescendIn
	 *            the array containing the subArray in which the root is to descend.
	 * @param lastInHeap
	 *            the index that indicates the last in the subArray.
	 * @param on
	 *            since this method is called recursively this value is used to
	 *            specify where the compiler "is".
	 * 
	 */
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
		if (leftChild <= lastInHeap) {
			comparisons++;
			greatestIndex = toDescendIn[on] > toDescendIn[leftChild] ? on : leftChild;
		}
		if (rightChild <= lastInHeap) {
			comparisons++;
			greatestIndex = toDescendIn[greatestIndex] > toDescendIn[rightChild] ? greatestIndex : rightChild;
		}

		// swapping greatest element into the root
		int buffer = toDescendIn[on];
		toDescendIn[on] = toDescendIn[greatestIndex];
		toDescendIn[greatestIndex] = buffer;

		// descending the root in the part its been switched into unless it hasnt been
		// descended
		visualize(toDescendIn);

		if (greatestIndex != on)
			descendInHeap(toDescendIn, lastInHeap, greatestIndex);
	}

	public boolean isHeapValid(int[] toCheck, int lastInHeap) {
		for (int i = 0; i < lastInHeap; i++) {

			int leftChild = 2 * (i + 1) - 1;
			int rightChild = 2 * (i + 1);

			if (leftChild <= lastInHeap) {
				comparisons++;
				if (toCheck[i] < toCheck[leftChild])
					return false;
			}
			if (rightChild <= lastInHeap) {
				comparisons++;
				if (toCheck[i] < toCheck[rightChild])
					return false;
			}
		}
		return true;
	}

	// DEBUG only
	/**
	 * Method not directly used. That returns the largest Value in an array.
	 * 
	 * @param toCheckIn
	 *            the array that is to be checked.
	 * @return the largest value in the array.
	 */
	public int getLargestValueOf(int[] toCheckIn) {
		int largestValue = 0;

		for (int i : toCheckIn)
			if (i > largestValue)
				largestValue = i;

		return largestValue;
	}

	/**
	 * Method implementing waiting times and drawing to the JLabel.
	 * 
	 * @param toSort
	 *            the array that is to be visualized by the JLabel.
	 */
	private void visualize(int[] toSort) {
		if (multiComparisons)
			return;
		if (sm.isVisualized() || !multiComparisons) {
			sl.visualize(toSort, sm.elements);
			sm.setTime(System.currentTimeMillis() - timeStarted);
			sm.setComparisons(comparisons);

			toWait += sm.getWait();

			// waiting in 100ms intervals
			long toWaitL = (long) toWait;
			while (toWaitL > 100) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				toWait -= 100;
				toWaitL = (long) toWait;
			}
			if (toWait > 0) {
				try {
					Thread.sleep((long) toWait);
				} catch (InterruptedException e) {
				}
				toWait -= (long) toWait;
			}
		}
	}

	/**
	 * Updates the wait time. This might abort the current wait cycle.
	 */
	public void abortCurrentWaiting() {
		toWait = sm.getWait();
	}
}
