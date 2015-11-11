package org.protege.editor.core.ui.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 21-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class JOptionPaneEx {

    private static final Logger logger = LoggerFactory.getLogger(JOptionPaneEx.class);


    public static int showConfirmDialog(Component parent, String title, JComponent content, int messageType, int optionType, final JComponent defaultFocusedComponent) {

        JOptionPane optionPane = new JOptionPane(content, messageType, optionType) {
            @Override
            public void selectInitialValue() {
                // This makes focusing more consistent between
                // various operating systems / window managers
                if(defaultFocusedComponent != null) {
                    defaultFocusedComponent.requestFocusInWindow();
                }
            }
        };

        JDialog dlg = createDialog(parent, title, optionPane, defaultFocusedComponent);
        dlg.setVisible(true);
        return getReturnValue(optionPane);
    }

    public static int showConfirmDialog(Component parent, String title, JComponent content, int messageType, int optionType, final JComponent defaultFocusedComponent, Object[] options, Object defaultOption) {

        JOptionPane optionPane = new JOptionPane(content, messageType, optionType, null, options, defaultOption) {
            @Override
            public void selectInitialValue() {
                // This makes focusing more consistent between
                // various operating systems / window managers
                if(defaultFocusedComponent != null) {
                    defaultFocusedComponent.requestFocusInWindow();
                }
            }
        };

        JDialog dlg = createDialog(parent, title, optionPane, defaultFocusedComponent);
        dlg.setVisible(true);
        return getReturnValue(optionPane);
    }


    public static int showValidatingConfirmDialog(Component parent, String title, JComponent component, int messageType, int optionType, final JComponent defaultFocusedComponent) {
        if (component instanceof VerifiedInputEditor) {
            final VerifyingOptionPane optionPane = new VerifyingOptionPane(component, messageType, optionType) {
                /**
                 *
                 */
                private static final long serialVersionUID = 7128847118051849761L;

                public void selectInitialValue() {
                    // This is overridden so that the option pane dialog default
                    // button doesn't get the focus.
                    if (defaultFocusedComponent != null) {
                        defaultFocusedComponent.requestFocusInWindow();
                    }
                }
            };
            final InputVerificationStatusChangedListener verificationListener = new InputVerificationStatusChangedListener() {
                public void verifiedStatusChanged(boolean verified) {
                    optionPane.setOKEnabled(verified);
                }
            };
            ((VerifiedInputEditor) component).addStatusChangedListener(verificationListener);

            final JDialog dlg = createDialog(parent, title, optionPane, defaultFocusedComponent);
            dlg.setModal(true);
            dlg.setVisible(true);
            return getReturnValue(optionPane);
        }
        else {
            logger.warn("Component should implement VerifiedInputEditor for validating dialog to work. " + "Using normal dialog with no validating");
            return showConfirmDialog(parent, title, component, messageType, optionType, null);
        }
    }


    private static JDialog createDialog(Component parent, String title, JOptionPane optionPane, final JComponent defaultFocusedComponent) {
        JDialog dlg = optionPane.createDialog(parent, title);
        dlg.setLocationRelativeTo(parent);
        dlg.setResizable(true);
        dlg.pack();
        return dlg;
    }


    private static int getReturnValue(JOptionPane optionPane) {
        Object value = optionPane.getValue();
        if (value != null && optionPane.getOptions() != null){
            value = Arrays.binarySearch(optionPane.getOptions(), value);
        }
        return (value != null) ? (Integer) value : JOptionPane.CLOSED_OPTION;
    }

}
