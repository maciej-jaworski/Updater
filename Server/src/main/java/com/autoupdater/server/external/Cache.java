package com.autoupdater.server.external;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Contains output cache (O type) for specified input (i type). 
 * 
 * Output is obtained through CacheSource<I,O> object.
 * 
 * @param <I> input type
 * @param <O> output type
 */
public class Cache <I extends Comparable <I>, O> {
	/**
	 * Runtime instance.
	 */
	private Runtime rt;
	
	/**
	 * Source object.
	 */
	private CacheSource <I, O>		Source;
	
	/**
	 * Maximal number of objects kept after cleanup.
	 */
	private int						MaxKeep;
	
	/**
	 * Amount of memory in bytes above which runCleanup() is called.
	 */
	private long					MaxMemory;
	
	/**
	 * Interval between cleanups.
	 */
	private int 					GCDelay;
	
	/**
	 * Cleanup cycles before request list reset.
	 */
	private int 					ResetCounter;
	
	/**
	 * Cleanups since reset.
	 */
	private int						CleanupCounter;
	
	/**
	 * Contains data about input and output.
	 * 
	 * @see CacheLeaf
	 */
	private Map <I, CacheLeaf> 		OutputData;
	
	/**
	 * Contains data about requests.
	 * 
	 * @see RequestData
	 */
	private ArrayList <RequestData>	OutputRequests;
	
	/**
	 * Creates Cache instance using passed source.
	 * 
	 * @param source CacheSource object
	 */
	public Cache (CacheSource<I, O> source) {
		initialize (source, true);
	}
	
	/**
	 * Creates Cache instance using passed source.
	 * 
	 * @param source CacheSource object
	 * @param boolean whether or not create Cleaner thread
	 */
	public Cache (CacheSource<I, O> source, boolean createCleaner) {
		initialize (source, createCleaner);
	}
	
	/**
	 * Sets interval between garbage collector calls.
	 * 
	 * @param gCDelay interval between garbage collector calls in ms 
	 * @return this object
	 */
	public Cache <I, O> setGCDelay (int gCDelay) {
		GCDelay = gCDelay;
		return this;
	}
	
	/**
	 * Sets maximal amount of cached object kept after cleaning.
	 * 
	 * @param maxKeep maximal amount of cached object kept after cleaning
	 * @return this object
	 */
	public Cache <I, O> setMaxKeep (int maxKeep) {
		MaxKeep = maxKeep;
		return this;
	}
	
	/**
	 * Sets maximal amount of memory in bytes above which runCleanup() is called.
	 * 
	 * @param maxMemory maximal amount of memory in bytes above which runCleanup() is called
	 * @return this object
	 */
	public Cache <I, O> setMaxMemory (long maxMemory) {
		MaxMemory = maxMemory;
		return this;
	}
	
	/**
	 * Sets number of cleaning cycles after which request list is reset.
	 * 
	 * @param resetCounter number of cycles after which list is reset
	 * @return this object
	 */
	public Cache <I, O> setResetCounter (int resetCounter) {
		ResetCounter = resetCounter;
		return this;
	}
	
	/**
	 * Obtains output for passed input.
	 * 
	 * @param input input data
	 * @return output data
	 */
	public O getDataFor (I input) {
		O output;
		synchronized (getMutex (input)) {
			refreshDataFor (input);
			output = OutputData.get (input).getOutput ();
		}
		if (MaxMemory > 0 && rt.totalMemory () - rt.freeMemory () > MaxMemory)
			runCleanup ();
		return output;
	}
	
	/**
	 * Force reload of an input.
	 * 
	 * For cases like change of the original value.
	 * 
	 * @param input
	 */
	public void forceReload (I input) {
		synchronized (getMutex (input)) {
			getLeaf (input).setOutput (null);
		}
	}
	
	/**
	 * Run cache cleanup - after certain cleanup also run a request reset.
	 * 
	 * @see Cache#runGC()
	 * @see Cache#runReset()
	 */
	public void runCleanup () {
		runGC ();
		
		if (++CleanupCounter >= ResetCounter) {
			runReset ();
			CleanupCounter = 0;
		}
	}
	
	/**
	 * Cache cleanup.
	 * 
	 * Saves first MaxKeep elements.
	 * 
	 * @see Cache#setMaxKeep(int)
	 */
	public void runGC () {
		if (OutputRequests.size () > MaxKeep) {
			for (int i = MaxKeep > 0 ? MaxKeep : 0; i < OutputRequests.size (); i++) {
				CacheLeaf Leaf = OutputRequests.get (i).getLeaf ();
				synchronized (getMutex (Leaf.getInput ())) {
					Leaf.setOutput (null);
				}
			}
			Runtime.getRuntime ().gc ();
		}
	}
	
	/**
	 * Request list reset.
	 * 
	 * @see Cache#setResetCounter(int)
	 */
	public void runReset () {
		for (int i = 0; i < OutputRequests.size (); i++)
			synchronized (OutputRequests) {
				OutputRequests.get (i).setRequests (0);
			}
	}
	
	/**
	 * Initializer called by constructors.
	 * 
	 * @param source CacheSource object
	 * @param createCleaner whether or not create Cleaner thread
	 */
	private void initialize (CacheSource <I, O> source, boolean createCleaner) {
		Source = source;
		setGCDelay (1000*3600);
		setMaxKeep (100);
		setResetCounter (24);
		setMaxMemory (0);
		OutputData = new TreeMap <I, CacheLeaf> ();
		OutputRequests = new ArrayList <RequestData> ();
		CleanupCounter = 0;
		
		rt = Runtime.getRuntime ();
		
		if (createCleaner)
			new Cleaner ().start ();
	}
	
	/**
	 * Obtains leaf from ObjectData tree (or create it) for passed input.
	 * 
	 * @param input input data
	 * @return leaf with information
	 */
	
	private CacheLeaf getLeaf (I input) {
		if (OutputData.containsKey (input))
			return OutputData.get (input);
		else {
			CacheLeaf Leaf = new CacheLeaf (input);
			OutputData.put(input, Leaf);
			return Leaf;
		}
	}
	
	
	/**
	 * Obtains mutex for passed input to separate 
	 * @param input input data
	 * @return mutex
	 */
	private synchronized Object getMutex (I input) {
		return getLeaf (input);
	}
	
	
	/**
	 * Updates request data and load object into cache if necessary.
	 * 
	 * @param input input data
	 */
	private void refreshDataFor (I input) {
		CacheLeaf Leaf = getLeaf (input);
		int CurrentPosition = Leaf.getCurrentPosition ();
		
		synchronized (OutputRequests) {
			if (CurrentPosition < 0) {
				OutputRequests.add (new RequestData (Leaf));
				Leaf.setCurrentPosition (OutputRequests.size ()-1);
				CurrentPosition = Leaf.getCurrentPosition ();
			}
		
			OutputRequests.get (CurrentPosition).increment ();
			while (CurrentPosition > 0 && OutputRequests.get (CurrentPosition-1).getRequests () < OutputRequests.get (CurrentPosition).getRequests ()) {
				Collections.swap (OutputRequests, CurrentPosition-1, CurrentPosition);
				OutputRequests.get (CurrentPosition-1).getLeaf ().setCurrentPosition (CurrentPosition);
				OutputRequests.get (CurrentPosition).getLeaf ().setCurrentPosition (CurrentPosition-1);
				CurrentPosition--;
			}
		}
			
		if (Leaf.getOutput () == null)
			Leaf.setOutput (Source.getElement (input));
	}
	
	/**
	 * Displays current state of cache.
	 */
	
	public void debugContent () {
		System.out.println ("Requests data:");
		System.out.println ("\tInput\t\tOutput (null for deleted cache)");
		for (Entry<I, CacheLeaf> Leaf : OutputData.entrySet ())
			System.out.println ("\t" + Leaf.getKey () + "" + "\t->\t" + Leaf.getValue ().getOutput ());
		
		System.out.println ("Requests list:");
		System.out.println ("\tReq.\t\tInput\t\tOutput (null for deleted cache)");
		for (RequestData Element : OutputRequests)
			System.out.println ("\t" + Element.getRequests() + "\t->\t" + Element.getLeaf ().getInput () + "\t->\t" + Element.getLeaf ().getOutput ());
	}
	
	/**
	 * Leaf in tree (OutputData).
	 */
	
	private class CacheLeaf {
		/**
		 * Input data.
		 */
		private I Input;
		
		/**
		 * Output data (actual cache)
		 */
		private O Output;
		
		/**
		 * Current position in request list.
		 */
		private int CurrentPosition;
		
		/**
		 * Creates leaf.
		 * 
		 * @param input input data
		 */
		public CacheLeaf (I input) {
			initialize (input);
		}
		
		/**
		 * Input getter.
		 * 
		 * @return input data
		 */
		public I getInput () {
			return Input;
		}
		
		/**
		 * Output getter.
		 * 
		 * @return output data
		 */
		public O getOutput () {
			return Output;
		}
		
		/**
		 * Output setter.
		 * 
		 * @param output output data
		 */
		public void setOutput (O output) {
			Output = output;
		}
		
		/**
		 * Gets current position of input in request list.
		 * 
		 * @return current position
		 */
		public int getCurrentPosition () {
			return CurrentPosition;
		}
		
		/**
		 * Sets current position of input in request list.
		 * 
		 * @param currentPosition current position
		 */
		public void setCurrentPosition (int currentPosition) {
			CurrentPosition = currentPosition;
		}
		
		/**
		 * Called by constructor.
		 * 
		 * @param input input data
		 */
		private void initialize (I input) {
			Input = input;
			Output = null;
			setCurrentPosition (-1);
		}
	}
	
	/**
	 * Element of request list (OutputRequest).
	 */
	
	private class RequestData {
		/**
		 * Leaf with data.
		 * 
		 * @see CacheLeaf
		 */
		private CacheLeaf Leaf;
		
		/**
		 * Number of requests.
		 */
		private int Requests;
		
		/**
		 * Creates RequestData.
		 * 
		 * @param leaf leaf for a request
		 */
		public RequestData (CacheLeaf leaf) {
			Leaf = leaf;
			Requests = 0;
		}
		
		/**
		 * Returns leaf for a request.
		 * 
		 * @return leaf with data
		 */
		public CacheLeaf getLeaf () {
			return Leaf;
		}
		
		/**
		 * Gets number of requests.
		 * 
		 * @return number of requests
		 */
		public int getRequests () {
			return Requests;
		}
		
		/**
		 * Sets requests to specified number.
		 * 
		 * @param requests number of requests
		 */
		public void setRequests (int requests) {
			Requests = requests;
		}
		
		/**
		 * Increment requests.
		 */
		public void increment () {
			Requests++;
		}
	}
	
	/**
	 * Calls runGC() and runReset() after specified time.
	 * 
	 *  @see Cache#runGC()
	 *  @see Cache#runReset()
	 */
	private class Cleaner extends Thread {
		/**
		 * Thread's body.
		 */
		public void run () {
			while (true) {
				if (GCDelay > 0) {
					try {
						sleep (GCDelay);
					} catch (InterruptedException e) {}
					
					runCleanup ();
				} else
					try {
						sleep (1000);
					} catch (InterruptedException e) {}
			}
		}
	}
}