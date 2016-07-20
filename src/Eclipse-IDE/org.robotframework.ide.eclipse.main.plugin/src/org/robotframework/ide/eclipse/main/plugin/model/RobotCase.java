/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.model;

import static com.google.common.collect.Lists.newArrayList;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Position;
import org.rf.ide.core.testdata.model.AModelElement;
import org.rf.ide.core.testdata.model.FilePosition;
import org.rf.ide.core.testdata.model.ModelType;
import org.rf.ide.core.testdata.model.table.RobotExecutableRow;
import org.rf.ide.core.testdata.model.table.testcases.TestCase;
import org.rf.ide.core.testdata.model.table.testcases.TestCaseSetup;
import org.rf.ide.core.testdata.model.table.testcases.TestCaseTags;
import org.rf.ide.core.testdata.model.table.testcases.TestCaseTeardown;
import org.rf.ide.core.testdata.model.table.testcases.TestCaseTemplate;
import org.rf.ide.core.testdata.model.table.testcases.TestCaseTimeout;
import org.rf.ide.core.testdata.model.table.testcases.TestDocumentation;
import org.robotframework.ide.eclipse.main.plugin.RedImages;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class RobotCase extends RobotCodeHoldingElement {

    private static final long serialVersionUID = 1L;

    private TestCase testCase;

    RobotCase(final RobotCasesSection parent, final String name, final String comment) {
        super(parent, name, comment);
    }

    @Override
    public TestCase getLinkedElement() {
        return testCase;
    }

    @Override
    public String getName() {
        return testCase.getDeclaration().getText();
    }

    public void link(final TestCase testCase) {
        this.testCase = testCase;

        for (final RobotExecutableRow<TestCase> execRow : testCase.getTestExecutionRows()) {
            final String callName = execRow.getAction().getText().toString();
            final List<String> args = newArrayList(
                    Lists.transform(execRow.getArguments(), TokenFunctions.tokenToString()));
            final RobotKeywordCall call = new RobotKeywordCall(this, callName, args, "");
            call.link(execRow);
            getChildren().add(call);
        }
        // settings
        for (final TestDocumentation documentation : testCase.getDocumentation()) {
            final String name = documentation.getDeclaration().getText().toString();
            final List<String> args = newArrayList(
                    Lists.transform(documentation.getDocumentationText(), TokenFunctions.tokenToString()));
            final RobotDefinitionSetting setting = new RobotDefinitionSetting(this, omitSquareBrackets(name), args, "");
            setting.link(documentation);
            getChildren().add(setting);
        }
        for (final TestCaseTags tags : testCase.getTags()) {
            final String name = tags.getDeclaration().getText().toString();
            final List<String> args = newArrayList(Lists.transform(tags.getTags(), TokenFunctions.tokenToString()));
            final RobotDefinitionSetting setting = new RobotDefinitionSetting(this, omitSquareBrackets(name), args, "");
            setting.link(tags);
            getChildren().add(setting);
        }
        for (final TestCaseSetup setup : testCase.getSetups()) {
            final String name = setup.getDeclaration().getText().toString();
            final List<String> args = setup.getKeywordName() == null ? new ArrayList<String>()
                    : newArrayList(setup.getKeywordName().getText().toString());
            args.addAll(Lists.transform(setup.getArguments(), TokenFunctions.tokenToString()));
            final RobotDefinitionSetting setting = new RobotDefinitionSetting(this, omitSquareBrackets(name), args, "");
            setting.link(setup);
            getChildren().add(setting);
        }
        for (final TestCaseTemplate template : testCase.getTemplates()) {
            final String name = template.getDeclaration().getText().toString();
            final List<String> args = template.getKeywordName() == null ? new ArrayList<String>()
                    : newArrayList(template.getKeywordName().getText().toString());
            final RobotDefinitionSetting setting = new RobotDefinitionSetting(this, omitSquareBrackets(name), args, "");
            setting.link(template);
            getChildren().add(setting);
        }
        for (final TestCaseTimeout timeout : testCase.getTimeouts()) {
            final String name = timeout.getDeclaration().getText().toString();
            final List<String> args = timeout.getTimeout() == null ? new ArrayList<String>()
                    : newArrayList(timeout.getTimeout().getText().toString());
            args.addAll(Lists.transform(timeout.getMessage(), TokenFunctions.tokenToString()));
            final RobotDefinitionSetting setting = new RobotDefinitionSetting(this, omitSquareBrackets(name), args, "");
            setting.link(timeout);
            getChildren().add(setting);
        }
        for (final TestCaseTeardown teardown : testCase.getTeardowns()) {
            final String name = teardown.getDeclaration().getText().toString();
            final List<String> args = teardown.getKeywordName() == null ? new ArrayList<String>()
                    : newArrayList(teardown.getKeywordName().getText().toString());
            args.addAll(Lists.transform(teardown.getArguments(), TokenFunctions.tokenToString()));
            final RobotDefinitionSetting setting = new RobotDefinitionSetting(this, omitSquareBrackets(name), args, "");
            setting.link(teardown);
            getChildren().add(setting);
        }

        Collections.sort(getChildren(), new Comparator<RobotKeywordCall>() {
            @Override
            public int compare(final RobotKeywordCall call1, final RobotKeywordCall call2) {
                final int offset1 = call1.getLinkedElement().getDeclaration().getStartOffset();
                final int offset2 = call2.getLinkedElement().getDeclaration().getStartOffset();
                return offset1 - offset2;
            }
        });
    }

    @Override
    public RobotKeywordCall createKeywordCall(final int modelTableIndex, final int index) {
        final RobotKeywordCall call = new RobotKeywordCall(this, "", new ArrayList<String>(), "");

        final RobotExecutableRow<TestCase> robotExecutableRow = new RobotExecutableRow<>();
        getLinkedElement().addTestExecutionRow(robotExecutableRow);
        call.link(robotExecutableRow);

        getChildren().add(index, call);
        return call;
    }

    @Override
    public void insertKeywordCall(final int modelTableIndex, final int codeHoldingElementIndex,
            final RobotKeywordCall keywordCall) {

    }

    private static String omitSquareBrackets(final String nameInBrackets) {
        return nameInBrackets.substring(1, nameInBrackets.length() - 1);
    }

    @Override
    public RobotCasesSection getParent() {
        return (RobotCasesSection) super.getParent();
    }

    @Override
    public ImageDescriptor getImage() {
        return testCase != null && testCase.isDataDrivenTestCase() ? RedImages.getTemplatedTestCaseImage()
                : RedImages.getTestCaseImage();
    }

    public List<RobotDefinitionSetting> getTagsSetting() {
        return findSettings(ModelType.TEST_CASE_TAGS);
    }

    private List<RobotDefinitionSetting> findSettings(final ModelType modelType) {
        final List<RobotDefinitionSetting> matchingSettings = new ArrayList<>();
        for (final RobotKeywordCall call : getChildren()) {
            if (call instanceof RobotDefinitionSetting && call.getLinkedElement().getModelType() == modelType) {
                matchingSettings.add((RobotDefinitionSetting) call);
            }
        }
        return matchingSettings;
    }

    public Optional<String> getTemplateInUse() {
        return Optional.fromNullable(testCase.getTemplateKeywordName());
    }

    @Override
    public DefinitionPosition getDefinitionPosition() {
        return new DefinitionPosition(testCase.getTestName().getFilePosition(),
                testCase.getTestName().getText().length());
    }

    @Override
    public Position getPosition() {
        if (testCase != null) {
            final FilePosition begin = testCase.getBeginPosition();
            final FilePosition end = testCase.getEndPosition();
            return new Position(begin.getOffset(), end.getOffset() - begin.getOffset());
        }
        return new Position(0);
    }

    @SuppressWarnings("unchecked")
    private Object readResolve() throws ObjectStreamException {
        // after deserialization we fix parent relationship in direct children
        for (final RobotKeywordCall call : getChildren()) {
            ((AModelElement<TestCase>) call.getLinkedElement()).setParent(testCase);
            call.setParent(this);
        }
        return this;
    }

    @Override
    public String toString() {
        // for debugging purposes only
        return getName();
    }
}
