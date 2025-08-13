package ru.gmp.ml;

import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.function.BiConsumer;

public class MLWindow extends Frame {

    private Choice choice;

    public MLWindow() {
        setTitle("Mindustry launcher");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        Label resultLabel = new Label("Select game version", Label.CENTER);

        add(resultLabel);
    }
    public void addChoice(Collection<String> c) {
        choice = new Choice();
        for (String item : c) {
            choice.add(item);
        }
        choice.select(0);
        add(choice);
    }
    public void addButton(Main main, BiConsumer<Main, String> func) {
        Button button = new Button("Start");
        button.addActionListener(e -> {
            String selected = choice.getSelectedItem();
            func.accept(main, selected);
        });
        add(button);
    }
    public void start() {
        setVisible(true);
    }
    public void stop() {
        setVisible(false);
    }
}