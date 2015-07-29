package org.robotframework.ide.eclipse.main.plugin.tableeditor.settings;

import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.robotframework.ide.eclipse.main.plugin.RobotImages;
import org.robotframework.ide.eclipse.main.plugin.model.RobotSetting;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.ISectionFormFragment.MatcherProvider;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.MatchesHighlightingLabelProvider;

class GeneralSettingsArgsLabelProvider extends MatchesHighlightingLabelProvider {

    private final int index;

    GeneralSettingsArgsLabelProvider(final MatcherProvider matcherProvider, final int index) {
        super(matcherProvider);
        this.index = index;
    }

    @Override
    public Color getBackground(final Object element) {
        // FIXME : resource leak
        return getSetting(element) == null ? new Color(Display.getDefault(), 250, 250, 250) : null;
    }

    @Override
    public String getText(final Object element) {
        final RobotSetting setting = getSetting(element);
        if (setting == null) {
            return "";
        }
        final List<String> arguments = setting.getArguments();
        return index < arguments.size() ? arguments.get(index) : "";
    }

    @Override
    public StyledString getStyledText(final Object element) {
        return highlightMatches(new StyledString(getText(element)));
    }

    @Override
    public String getToolTipText(final Object element) {
        final String tooltipText = getText(element);
        return tooltipText.isEmpty() ? "<empty>" : tooltipText;
    }

    @Override
    public Image getToolTipImage(final Object object) {
        return RobotImages.getTooltipImage().createImage();
    }

    private RobotSetting getSetting(final Object element) {
        return (RobotSetting) ((Entry<?, ?>) element).getValue();
    }
}
