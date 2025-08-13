package ru.gmp.ml;

import javax.swing.*;
import java.awt.*;

class ProgressBar {
    private JDialog dialog;
    private JProgressBar progressBar;
    private final String baseTitle;

    public ProgressBar(String title) {
        this(title, null);
    }

    public ProgressBar(String title, Window owner) {
        this.baseTitle = title;

        SwingUtilities.invokeLater(() -> {
            if (owner instanceof Frame) {
                dialog = new JDialog((Frame) owner);
            } else if (owner instanceof Dialog) {
                dialog = new JDialog((Dialog) owner);
            } else {
                dialog = new JDialog();
            }

            dialog.setTitle(title);
            dialog.setSize(400, 150);
            dialog.setLayout(new BorderLayout());
            dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

            progressBar = new JProgressBar(0, 100);
            progressBar.setValue(0);
            progressBar.setStringPainted(true);
            progressBar.setFont(new Font("Arial", Font.BOLD, 14));
            progressBar.setForeground(new Color(50, 150, 250));
            progressBar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            dialog.add(progressBar, BorderLayout.CENTER);
        });
    }

    public void show() {
        SwingUtilities.invokeLater(() -> {
            if (dialog != null) {
                dialog.setLocationRelativeTo(dialog.getParent());
                dialog.setVisible(true);
            }
        });
    }

    public void hide() {
        SwingUtilities.invokeLater(() -> {
            if (dialog != null) {
                dialog.setVisible(false);
            }
        });
    }

    public void add(int value) {
        SwingUtilities.invokeLater(() -> {
            if (progressBar != null) {
                int newValue = Math.min(progressBar.getValue() + value, 100);
                progressBar.setValue(newValue);
                updateTitle();
            }
        });
    }

    public void set(int value) {
        SwingUtilities.invokeLater(() -> {
            if (progressBar != null) {
                int validatedValue = Math.max(0, Math.min(value, 100));
                progressBar.setValue(validatedValue);
                updateTitle();
            }
        });
    }

    public int get() {
        if (progressBar != null) {
            return progressBar.getValue();
        }
        return 0;
    }

    private void updateTitle() {
        SwingUtilities.invokeLater(() -> {
            if (dialog != null && progressBar != null) {
                dialog.setTitle(baseTitle + " - " + progressBar.getValue() + "%");
            }
        });
    }
}