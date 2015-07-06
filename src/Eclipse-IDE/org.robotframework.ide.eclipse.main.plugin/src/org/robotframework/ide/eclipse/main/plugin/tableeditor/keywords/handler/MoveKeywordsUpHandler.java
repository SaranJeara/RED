package org.robotframework.ide.eclipse.main.plugin.tableeditor.keywords.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.tools.compat.parts.DIHandler;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.robotframework.ide.eclipse.main.plugin.RobotKeywordCall;
import org.robotframework.ide.eclipse.main.plugin.RobotKeywordDefinition;
import org.robotframework.ide.eclipse.main.plugin.cmd.MoveKeywordCallUpCommand;
import org.robotframework.ide.eclipse.main.plugin.cmd.MoveKeywordDefinitionUpCommand;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.RobotEditorCommandsStack;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.keywords.handler.MoveKeywordsUpHandler.E4MoveKeywordUpHandler;
import org.robotframework.viewers.Selections;

import com.google.common.base.Optional;

public class MoveKeywordsUpHandler extends DIHandler<E4MoveKeywordUpHandler> {

    public MoveKeywordsUpHandler() {
        super(E4MoveKeywordUpHandler.class);
    }

    public static class E4MoveKeywordUpHandler {

        @Inject
        private RobotEditorCommandsStack commandsStack;

        @Execute
        public Object moveUp(@Named(Selections.SELECTION) final IStructuredSelection selection) {
            final Optional<RobotKeywordCall> maybeKeywordCall = Selections.getOptionalFirstElement(selection,
                    RobotKeywordCall.class);
            final Optional<RobotKeywordDefinition> maybeKeywordDef = Selections.getOptionalFirstElement(selection,
                    RobotKeywordDefinition.class);

            if (maybeKeywordCall.isPresent()) {
                commandsStack.execute(new MoveKeywordCallUpCommand(maybeKeywordCall.get()));
            } else if (maybeKeywordDef.isPresent()) {
                commandsStack.execute(new MoveKeywordDefinitionUpCommand(maybeKeywordDef.get()));
            }
            return null;
        }
    }
}
