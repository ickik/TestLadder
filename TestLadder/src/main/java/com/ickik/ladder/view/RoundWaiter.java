package com.ickik.ladder.view;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class RoundWaiter extends JComponent {

    final int N;
    int currentCompositeStart = 0;
    final AlphaComposite[] composites;

    final Image image;
    
    public RoundWaiter(ImageIcon icon,int n) {
    	if (icon!=null) {
            this.image=icon.getImage();
            setPreferredSize(new Dimension(icon.getIconWidth(),icon.getIconHeight()));
        } else {
            image=null;
            setPreferredSize(new Dimension(200,200));
        }
        if (n<=0) {
            throw new IllegalArgumentException("n must be >0");
        }
        N=n;
        composites = new AlphaComposite[N];
        for (int i = 0; i < N; i++) {
            composites[i] = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1 - ((float) i / (float) N));
        }
        new Timer(1000/N, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentCompositeStart = (currentCompositeStart + 1) % N;
                paintImmediately(0, 0, getWidth(), getHeight());
            }
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int width=getWidth();
        int height=getHeight();
        if (image==null) {
//            if (isOpaque()) {
//                g2.setColor(getBackground());
//                g2.fillRect(0,0,width,height);
//                //g2.fillOval(0, 0, width, height);
//            }
        } else {
            g2.drawImage(image,0,0,width,height,null);
        }
        int xCenter = width / 2;
        int yCenter = height / 2;
        int radius=(int)((width<height)?width/2.6:height/2.6);
        int size=radius/3;
        g2.setColor(Color.RED);
        AffineTransform old = g2.getTransform();
        for (int i = 0; i < N; i++) {
            g2.setComposite(composites[(currentCompositeStart + i) % N]);
            g2.translate(xCenter,yCenter);
            g2.rotate(2 * Math.PI * i / N);
            //g2.fillRect(radius-size/2,-size/2, size, size);
            g2.fillOval(radius-size/2,-size/2, size, size);
            g2.setTransform(old);
        }
    }

}
