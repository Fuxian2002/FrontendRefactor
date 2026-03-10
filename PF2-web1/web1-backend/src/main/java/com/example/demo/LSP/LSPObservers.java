package com.example.demo.LSP;

import java.util.concurrent.CopyOnWriteArraySet;

public class LSPObservers {
	private static CopyOnWriteArraySet<LSPObserver> observerSet = new CopyOnWriteArraySet<LSPObserver>();

	public static CopyOnWriteArraySet<LSPObserver> getObserverSet() {
		return observerSet;
	}

	public static void setObserverSet(CopyOnWriteArraySet<LSPObserver> observerSet) {
		LSPObservers.observerSet = observerSet;
	}
}
