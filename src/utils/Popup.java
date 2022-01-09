package utils;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Ma7MOoOD SHaRaF
 */

public class Popup {
    JFrame f = new JFrame();
    String soundPath;
    String imgPath;
    MediaPlayer player;
    final FileUtils utils;
    final Dimension screenSize;

    public Popup(String soundPath, String imgPath, FileUtils fileUtils) {
        this.soundPath = soundPath;
        this.imgPath = imgPath;
        this.utils = fileUtils;
        this.screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    }


    JLabel getImageLbl(String path) {
        try {
            BufferedImage buff = ImageIO.read(new File(path));
            ImageIcon icon = new ImageIcon(buff);
            JLabel iconLbl = new JLabel(icon);
            iconLbl.setSize(buff.getWidth(), buff.getHeight());
            return iconLbl;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            utils.writeLog("Exception on getImageLbl - read image from path \"" + path + "\" : " + e.getMessage());
            return new JLabel();
        }
    }

    public void show() {
        if (soundPath != null) {
            JFXPanel f1 = new JFXPanel();
            player = new MediaPlayer(new Media(new File(soundPath).toURI().toString()));
            player.play();
        }
        if (imgPath != null) {
            JLabel imgLbl = getImageLbl(imgPath);
            imgLbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    close();
                }
            });
            JPanel panel = new JPanel();
            panel.add(imgLbl);
            panel.setOpaque(false);
            f.setSize(imgLbl.getWidth(), imgLbl.getHeight());
            f.setLayout(null);
            f.setUndecorated(true);
            f.setBackground(new Color(0, 0, 0, 0));
            f.setContentPane(panel);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setAlwaysOnTop(true);
            f.setResizable(false);
            f.setLocationRelativeTo(null);
            f.setLocation((int) screenSize.getWidth() - imgLbl.getWidth() - 10, (int) screenSize.getHeight() - imgLbl.getHeight() - 50);
            f.setVisible(true);
        }
    }

    public void close() {
        try {
            if (f != null) {
                f.dispose();
            }
            if (player != null) {
                player.stop();
                player.dispose();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            utils.writeLog("Exception on close popup: " + ex.getMessage());
        } finally {
            System.out.println("Stopped");
        }
    }
}