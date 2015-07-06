package org.robotframework.ide.eclipse.main.plugin.tableeditor.keywords.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tools.compat.parts.DIHandler;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.dnd.Clipboard;
import org.robotframework.ide.eclipse.main.plugin.RobotKeywordCall;
import org.robotframework.ide.eclipse.main.plugin.RobotKeywordDefinition;
import org.robotframework.ide.eclipse.main.plugin.RobotKeywordsSection;
import org.robotframework.ide.eclipse.main.plugin.RobotSuiteFile;
import org.robotframework.ide.eclipse.main.plugin.cmd.InsertKeywordCallsCommand;
import org.robotframework.ide.eclipse.main.plugin.cmd.InsertKeywordDefinitionsCommand;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.RobotEditorCommandsStack;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.RobotEditorSources;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.dnd.KeywordCallsTransfer;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.dnd.KeywordDefinitionsTransfer;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.keywords.handler.PasteKeywordsHandler.E4PasteKeywordsHandler;
import org.robotframework.viewers.Selections;

public class PasteKeywordsHandler extends DIHandler<E4PasteKeywordsHandler> {
    public PasteKeywordsHandler() {
        super(E4PasteKeywordsHandler.class);
    }

    public static class E4PasteKeywordsHandler {

        @Inject
        @Named(RobotEditorSources.SUITE_FILE_MODEL)
        private RobotSuiteFile fileModel;

        @Inject
        private RobotEditorCommandsStack commandsStack;

        @Execute
        public Object pasteKeywords(@Named(Selections.SELECTION) final ITreeSelection selection,
                final Clipboard clipboard) {
            final Object probablyKeywordDefs = clipboard.getContents(KeywordDefinitionsTransfer.getInstance());

            if (probablyKeywordDefs instanceof RobotKeywordDefinition[]) {
                insertDefinitions(selection, (RobotKeywordDefinition[]) probablyKeywordDefs);
                return null;
            }

            final Object probablyKeywordCalls = clipboard.getContents(KeywordCallsTransfer.getInstance());
            if (probablyKeywordCalls instanceof RobotKeywordCall[]) {
                insertCalls(selection, (RobotKeywordCall[]) probablyKeywordCalls);
            }
            return null;
        }

        private void insertDefinitions(final ITreeSelection selection, final RobotKeywordDefinition[] definitions) {
            final TreePath selectedPath = Selections.getFirstElementPath(selection);
            final RobotKeywordDefinition targetDef = getElementOfClass(selectedPath, RobotKeywordDefinition.class);

            if (targetDef != null) {
                final int index = targetDef.getParent().getChildren().indexOf(targetDef);
                commandsStack.execute(new InsertKeywordDefinitionsCommand((RobotKeywordsSection) targetDef.getParent(),
                        index, definitions));
            } else {
                final RobotKeywordsSection section = (RobotKeywordsSection) fileModel.findSection(
                        RobotKeywordsSection.class).orNull();
                if (section != null) {
                    commandsStack.execute(new InsertKeywordDefinitionsCommand(section, definitions));
                }
            }
        }

        private void insertCalls(final ITreeSelection selection, final RobotKeywordCall[] calls) {
            final TreePath selectedPath = Selections.getFirstElementPath(selection);

            if (selectedPath.getSegmentCount() > 0) {
                final RobotKeywordCall targetCall = getElementOfClass(selectedPath, RobotKeywordCall.class);
                if (targetCall != null) {
                    final int index = targetCall.getParent().getChildren().indexOf(targetCall);
                    commandsStack.execute(new InsertKeywordCallsCommand(targetCall.getParent(), index, calls));
                } else {
                    final RobotKeywordDefinition targetDef = getElementOfClass(selectedPath,
                            RobotKeywordDefinition.class);
                    if (targetDef != null) {
                        commandsStack.execute(new InsertKeywordCallsCommand(targetDef, calls));
                    }
                }
            }
        }

        private static <T> T getElementOfClass(final TreePath path, final Class<? extends T> clazz) {
            if (path.getSegmentCount() == 0) {
                return null;
            }
            for (int i = path.getSegmentCount() - 1; i >= 0; i--) {
                final Object current = path.getSegment(i);
                if (clazz.isInstance(current)) {
                    return clazz.cast(current);
                }
            }
            return null;
        }
    }
}
