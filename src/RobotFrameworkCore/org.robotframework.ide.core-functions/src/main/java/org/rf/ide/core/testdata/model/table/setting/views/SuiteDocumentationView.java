/*
 * Copyright 2016 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.rf.ide.core.testdata.model.table.setting.views;

import java.util.List;

import org.rf.ide.core.testdata.model.table.setting.SuiteDocumentation;
import org.rf.ide.core.testdata.text.read.recognizer.RobotToken;

public class SuiteDocumentationView extends SuiteDocumentation {

    private final List<SuiteDocumentation> suiteDocs;

    public SuiteDocumentationView(final List<SuiteDocumentation> suiteDocs) {
        super(suiteDocs.get(0).getDeclaration());
        this.suiteDocs = suiteDocs;
    }

    @Override
    public void addDocumentationText(final String text) {
        joinIfNeeded();
        super.addDocumentationText(text);
    }

    @Override
    public void addDocumentationText(final RobotToken token) {
        joinIfNeeded();
        super.addDocumentationText(token);
    }

    @Override
    public void setDocumentationText(final int index, final String docText) {
        joinIfNeeded();
        super.setDocumentationText(index, docText);
    }

    @Override
    public void setDocumentationText(final int index, final RobotToken docText) {
        joinIfNeeded();
        super.setDocumentationText(index, docText);
    }

    @Override
    public void addCommentPart(final RobotToken rt) {
        joinIfNeeded();
        super.addCommentPart(rt);
    }

    @Override
    public void setComment(final String comment) {
        joinIfNeeded();
        super.setComment(comment);
    }

    @Override
    public void setComment(final RobotToken rt) {
        joinIfNeeded();
        super.setComment(rt);
    }

    private synchronized void joinIfNeeded() {
        if (suiteDocs.size() > 1) {
            SuiteDocumentation joined = new SuiteDocumentation(getDeclaration());
            joinDoc(joined, suiteDocs);
            suiteDocs.clear();
            suiteDocs.add(joined);
        }
    }

    private void joinDoc(final SuiteDocumentation target, final List<SuiteDocumentation> suiteDocs) {
        for (final SuiteDocumentation sd : suiteDocs) {
            for (final RobotToken text : sd.getDocumentationText()) {
                sd.addDocumentationText(text);
            }

            for (final RobotToken comment : sd.getComment()) {
                sd.addCommentPart(comment);
            }
        }
    }
}
