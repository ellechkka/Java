import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class lab4 extends JFrame {
    private JPanel panel = new JPanel(new GridLayout(4, 4, 5, 5));
    private int[][] numbers = new int[4][4];

    public lab4() {
        super("Пятнашки");

        setBounds(0, 0, 400, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        setFocusable(true);

        addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                changeKey(e);
            }
        });

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        createMenu();

        Container container = getContentPane();
        panel.setDoubleBuffered(true);
        container.add(panel);

        generate();
        repaintField();
    }

    public void generate() {
        Random generator = new Random();
        int[] invariants = new int[16];
        do {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    numbers[i][j] = 0;
                    invariants[i * 4 + j] = 0;
                }
            }
            numbers[3][3] = -1;
            for (int i = 1; i < 16; i++) {
                int k, l;
                do {
                    k = generator.nextInt(4);
                    l = generator.nextInt(4);
                }
                while (numbers[k][l] != 0);
                numbers[k][l] = i;
                invariants[k * 4 + l] = i;
            }
            numbers[3][3] = 0;
        }
        while (!canBeSolved(invariants));
    }

    public boolean canBeSolved(int[] invariants) {
        int sum = 0;
        for (int i = 0; i < 16; i++) {
            if (invariants[i] == 0) {
                sum += i / 4;
                continue;
            }

            for (int j = i + 1; j < 16; j++) {
                if (invariants[j] < invariants[i])
                    sum ++;
            }
        }
        System.out.println(sum % 2 == 0);
        return sum % 2 == 0;
    }

    public void repaintField() {
        panel.removeAll();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Font font = new Font("Arial", Font.PLAIN, 46);
                JButton button = new JButton(Integer.toString(numbers[i][j]));
                button.setFont(font);
                panel.add(button);
                if (numbers[i][j] == 0) {
                    button.setVisible(false);
                } else
                    button.addActionListener(new ClickListener());
            }
        }
        panel.validate();
        panel.repaint();
    }

    public boolean checkWin() {
        boolean status = true;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 3 && j > 2)
                    break;
                if (numbers[i][j] != i * 4 + j + 1) {
                    status = false;
                }
            }
        }
        return status;
    }

    private void createMenu() {
        JMenuBar menu = new JMenuBar();
        setJMenuBar(menu);

        JMenu fileMenu = new JMenu("File");
        menu.add(fileMenu);
        fileMenu.setMnemonic('F');

        JMenuItem jmNew = new JMenuItem("New");
        JMenuItem jmExit = new JMenuItem("Exit");
        fileMenu.add(jmNew);
        fileMenu.addSeparator();
        fileMenu.add(jmExit);
        jmNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                InputEvent.CTRL_DOWN_MASK));
        jmNew.setMnemonic('N');
        jmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                InputEvent.CTRL_DOWN_MASK));
        jmExit.setMnemonic('E');
        jmExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        jmNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generate();
                repaintField();
            }
        });

        JMenu aboutMenu = new JMenu("About");
        menu.add(aboutMenu);
        aboutMenu.setMnemonic('A');


        JMenuItem jmAuthor = new JMenuItem("Author");
        aboutMenu.add(jmAuthor);
        jmAuthor.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                InputEvent.CTRL_DOWN_MASK));
        jmAuthor.setMnemonic('U');

        jmAuthor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AboutDialog dialog = new AboutDialog(lab4.this);
                dialog.setVisible(true);
            }
        });
    }

    public class AboutDialog extends JDialog {
        public AboutDialog(JFrame owner) {
            super(owner, "About", true);
            add(new JLabel("<html><hl>Романова Элина</hl><hr>"
                            + "P3168<hr>" + "2021 год</html>"),
                    BorderLayout.CENTER);
            JButton ok = new JButton("OK");
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    setVisible(false);
                }
            });
            JPanel p = new JPanel();
            p.add(ok);
            add(p, BorderLayout.SOUTH);
            setSize (200, 150);
            setLocationRelativeTo(null);
        }
    }

    private class ClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            button.setVisible(false);
            String name = button.getText();
            changeClick(Integer.parseInt(name));
        }
    }

    public void changeClick(int num) {
        int i = 0, j = 0;
        for (int k = 0; k < 4; k++) {
            for (int l = 0; l < 4; l++) {
                if (numbers[k][l] == num) {
                    i = k;
                    j = l;
                }
            }
        }
        if (i > 0) {
            if (numbers[i - 1][j] == 0) {
                numbers[i - 1][j] = num;
                numbers[i][j] = 0;
            }
        }
        if (i < 3) {
            if (numbers[i + 1][j] == 0) {
                numbers[i + 1][j] = num;
                numbers[i][j] = 0;
            }
        }
        if (j > 0) {
            if (numbers[i][j - 1] == 0) {
                numbers[i][j - 1] = num;
                numbers[i][j] = 0;
            }
        }
        if (j < 3) {
            if (numbers[i][j + 1] == 0) {
                numbers[i][j + 1] = num;
                numbers[i][j] = 0;
            }
        }

        repaintField();

        if (checkWin()) {
            JOptionPane.showMessageDialog(null, "Поздравляю!", "Вы выиграли", JOptionPane.INFORMATION_MESSAGE);
            generate();
            repaintField();
        }
    }

    public void changeKey(KeyEvent e) {

        int key = e.getKeyCode();
        int i = 0, j = 0;
        for (int k = 0; k < 4; k++)
            for (int l = 0; l < 4; l++)
                if (numbers[k][l] == 0) {
                    i = k;
                    j = l;
                }
        switch (key) {
            case KeyEvent.VK_UP:
                if (i == 3)
                    break;
                else {
                    numbers[i][j] = numbers[i + 1][j];
                    numbers[i + 1][j] = 0;
                    break;
                }
            case KeyEvent.VK_DOWN:
                if (i == 0)
                    break;
                else {
                    numbers[i][j] = numbers[i - 1][j];
                    numbers[i - 1][j] = 0;
                    break;
                }
            case KeyEvent.VK_LEFT:
                if (j == 3)
                    break;
                else {
                    numbers[i][j] = numbers[i][j + 1];
                    numbers[i][j + 1] = 0;
                    break;
                }
            case KeyEvent.VK_RIGHT:
                if (j == 0)
                    break;
                else {
                    numbers[i][j] = numbers[i][j - 1];
                    numbers[i][j - 1] = 0;
                    break;
                }
        }
        repaintField();
        if (checkWin()) {
            JOptionPane.showMessageDialog(null, "Поздравляю!", "Вы выиграли", JOptionPane.INFORMATION_MESSAGE);
            generate();
            repaintField();
        }
    }

    public static void main(String[] args) {
        JFrame app = new lab4();
        app.setVisible(true);
    }
}
