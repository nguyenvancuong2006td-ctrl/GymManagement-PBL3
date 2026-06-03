package util;
import javax.swing.*;
import java.awt.*;

public class RoundedButton extends JButton {

    private Color backgroundColor;
    private Color hoverColor;
    private boolean hover = false;

    public RoundedButton(String text, Color bg) {
        super(text);

        this.backgroundColor = bg;
        this.hoverColor = bg.brighter();

        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setForeground(Color.WHITE);

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Shadow
        g2.setColor(new Color(0, 0, 0, 35));
        g2.fillRoundRect(
                3, 3,
                getWidth() - 3,
                getHeight() - 3,
                20, 20
        );

        // Background
        g2.setColor(hover ? hoverColor : backgroundColor);
        g2.fillRoundRect(
                0, 0,
                getWidth() - 3,
                getHeight() - 3,
                20, 20
        );

        g2.dispose();

        super.paintComponent(g);
    }
}
