package com.example.demo.LSP;

import java.util.concurrent.CopyOnWriteArraySet;

public class LSPSubjects {
	// 每个项目的每种编辑器对应一个subject
	private static CopyOnWriteArraySet<LSPSubject> subjectSet = new CopyOnWriteArraySet<LSPSubject>();

	public static CopyOnWriteArraySet<LSPSubject> getSubjectSet() {
		return subjectSet;
	}

	public static void setSubjectSet(CopyOnWriteArraySet<LSPSubject> subjectSet) {
		LSPSubjects.subjectSet = subjectSet;
	}

}
