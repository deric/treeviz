/*
 * @(#)IcicleDraw.java  1.0.1  2011-08-20
 *
 * Copyright (c) 2007-2011 Werner Randelshofer, Goldau, Switzerland.
 * All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * license agreement you entered into with Werner Randelshofer.
 * For details see accompanying license terms.
 */

package ch.randelshofer.tree.sunburst;

import ch.randelshofer.tree.NodeInfo;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

/**
 * IcicleDraw draws a linear version of a {@link SunburstTree}.
 * <p>
 * Can draw the tree from any node within the tree.
 *
 * @author Werner Randelshofer
 * @version 1.0.1 2011-08-20 Fixes drawing of descendant subtree bounds.
 * <br>1.0 September 18, 2007 Created.
 */
public class IcicleDraw {
    /**
     * Center of the sunburst tree.
     */
    protected double cx = 100, cy = 100;
    
    /**
     * Inner and outer radius of the sunburst tree.
     */
    protected double width = 96, height = 96;
    
    /**
     * Root of the sunburst tree.
     */
    protected SunburstNode root;
    
    /**
     * Maximal depth of the sunburst tree.
     */
    protected int totalDepth;
    
    
    
    protected NodeInfo info;
    
    /*
    private int w = 200, h = 200;
    private SunburstTree model;
    private double radius;
    private double selectedAngleFactor;
    private double selectedMaxDepth;
     */
    
    /** Creates a new instance. */
    public IcicleDraw(SunburstTree model) {
        this(model.getRoot(), model.getInfo());
    }
    public IcicleDraw(SunburstNode root, NodeInfo info) {
        this.root = root;
        totalDepth = root.getMaxDepth();
        this.info = info;
    }
    
    public SunburstNode getRoot() {
        return root;
    }
    
    public int getTotalDepth() {
        return totalDepth;
    }
    
    public double getX() {
        return cx;
    }
    public void setX(double newValue) {
        cx = newValue;
    }
    
    public double getY() {
        return cy;
    }
    
    public void setY(double newValue) {
        cy = newValue;
    }
    
    public double getWidth() {
        return width;
    }
    
    public void setWidth(double newValue) {
        width = newValue;
    }
    
    public double getHeight() {
        return height;
    }
    
    public void setHeight(double newValue) {
        height = newValue;
    }
    
    public SunburstNode getNodeAt(int x, int y) {
        int depth = (int) ((x - cx) / width * getTotalDepth());
        long number = (long) ((y - cy) / height * root.getExtent()) + root.getLeft();
        return root.findNode(depth, number);
    }
    
    public String getToolTipText(int x, int y) {
        SunburstNode node = getNodeAt(x, y);
        return (node == null) ? null : info.getTooltip(node.getDataNodePath());
    }
    
    
    public void drawNodeBounds(Graphics2D g, SunburstNode node, Color color) {
        double h = height * node.getExtent() / root.getExtent();
        double less;
        if (h > 2 && node.getExtent() < root.getExtent()) {
            less = 0.5;
        } else {
            less = 0;
        }
        
        Rectangle2D.Double r = new Rectangle2D.Double(
                cx + width * (node.getDepth() - root.getDepth()) / totalDepth,
                cy + height * (node.getLeft() - root.getLeft()) / root.getExtent() + less,
                width / totalDepth - 1,
                height * node.getExtent() / root.getExtent() - less * 2
                );
        g.setColor(color);
        g.draw(r);
    }
    public void drawSubtreeBounds(Graphics2D g, SunburstNode node, Color color) {
        double h = height * node.getExtent() / root.getExtent();
        double less;
        if (h > 2 && node.getExtent() < root.getExtent()) {
            less = 0.5;
        } else {
            less = 0;
        }
        
        Rectangle2D.Double r = new Rectangle2D.Double(
                cx + width * node.getDepth() / totalDepth,
                cy + height * node.getLeft() / root.getExtent() + less,
                width - (width * node.getDepth() / totalDepth),
                height * node.getExtent() / root.getExtent() - less * 2
                );
        g.setColor(color);
        g.draw(r);
    }
    public void drawDescendantSubtreeBounds(Graphics2D g, SunburstNode node, Color color) {
        if (node.isLeaf()) {
            drawNodeBounds(g, node, color);
        } else {
            double h = height * node.getExtent() / root.getExtent();
            double less;
            if (h > 2 && node.getExtent() < root.getExtent()) {
                less = 0.5;
            } else {
                less = 0;
            }
            
            Rectangle2D.Double r = new Rectangle2D.Double(
                    cx + width * (node.getDepth() ) / totalDepth,
                    cy + height * node.getLeft() / root.getExtent() + less,
                    //width - (width * (node.getDepth() + 1) / totalDepth),
                    getRadius(node.getDepth() + node.getMaxDepth()) - getRadius(node.getDepth() ),
                    height * node.getExtent() / root.getExtent() - less * 2
                    );
            g.setColor(color);
            g.draw(r);
        }
    }
    
    public static Rectangle2D.Double createArc(double x, double y,
            double startAngle, double arc,
            double outerRadius, double innerRadius) {
        Rectangle2D.Double rec;
        
        rec = new Rectangle2D.Double(
                x + innerRadius, y + startAngle,
                outerRadius - innerRadius, arc
                );
        return rec;
        
        
        /*
        GeneralPath mc = new GeneralPath();
         
        // if yRadius is undefined, yRadius = radius
        // Init vars
        double segAngle_a, segAngle_b, theta_a, theta_b, angle, angleMid, segs;
        double ax, ay, bx, by, cx, cy, dx, dy;
         
        // init bx and by, altough they are always initialized
        bx = by = 0;
         
        // limit sweep to reasonable numbers
        if (Math.abs(arc) > 360) {
            arc = 360;
        }
        // Flash uses 8 segments per circle, to match that, we draw in a maximum
        // of 45 degree segments. First we calculate how many segments are needed
        // for our arc.
        segs = Math.ceil(Math.abs(arc) / 45);
        // Now calculate the sweep of each segment.
        segAngle_a = arc / segs;
        segAngle_b = -arc / segs;
        // The math requires radians rather than degrees. To convert from degrees
        // use the formula (degrees/180)*Math.PI to get radians.
        theta_a = - (segAngle_a / 180) * Math.PI;
        theta_b = - (segAngle_b / 180) * Math.PI;
        // convert angle startAngle to radians
        angle = - (startAngle / 180) * Math.PI;
        // draw the curve in segments no larger than 45 degrees.
        if (segs > 0) {
            // draw a line from the end of the interior curve to the start of the exterior curve
            ax = x + Math.sin( - startAngle / 180 * Math.PI) * outerRadius;
            ay = y + Math.cos( startAngle / 180 * Math.PI) * outerRadius;
            mc.moveTo((float) ax, (float)ay);
            // Loop for drawing exterior  curve segments
            for (int i = 0; i < segs; i ++) {
                angle += theta_a;
                angleMid = angle - (theta_a / 2);
                bx = x + Math.sin(angle) * outerRadius;
                by = y + Math.cos(angle) * outerRadius;
                cx = x + Math.sin(angleMid) * (outerRadius / Math.cos(theta_a / 2));
                cy = y + Math.cos(angleMid) * (outerRadius / Math.cos(theta_a / 2));
                mc.quadTo((float)cx, (float)cy, (float)bx, (float)by);
            }
            // draw a line from the end of the exterior curve to the start of the interior curve
         
            startAngle += arc;
            angle = - (startAngle / 180) * Math.PI;
            // draw the interior (subtractive) wedge
            // draw a line from the center to the start of the interior curve
            dx = x + Math.sin( - startAngle / 180 * Math.PI) * innerRadius;
            dy = y + Math.cos( startAngle / 180 * Math.PI) * innerRadius;
            if (arc < 360) {
                mc.lineTo((float)dx, (float)dy);
            } else {
                mc.moveTo((float)dx, (float)dy);
            }
            if (innerRadius > 0) {
                // Loop for drawing interior curve segments
                for (int i = 0; i < segs; i ++) {
                    angle += theta_b;
                    angleMid = angle - (theta_b / 2);
                    bx = x + Math.sin(angle) * innerRadius;
                    by = y + Math.cos(angle) * innerRadius;
                    cx = x + Math.sin(angleMid) * (innerRadius / Math.cos(theta_b / 2));
                    cy = y + Math.cos(angleMid) * (innerRadius / Math.cos(theta_b / 2));
                    mc.quadTo((float)cx, (float)cy, (float)bx, (float)by);
                }
            }
            if (arc < 360) {
                mc.lineTo((float)ax, (float)ay);
            }
        }
        return mc;
         */
    }
    public static void addSeg(GeneralPath mc, double x, double y,
            double startAngle, double arc,
            double radius) {
        
        // Init vars
        double segAngle_a, segAngle_b, theta_a, theta_b, angle, angleMid, segs;
        double ax, ay, bx, by, cx, cy, dx, dy;
        
        // init bx and by, altough they are always initialized
        bx = by = 0;
        
        // limit sweep to reasonable numbers
        if (Math.abs(arc) > 360) {
            arc = 360;
        }
        // Flash uses 8 segments per circle, to match that, we draw in a maximum
        // of 45 degree segments. First we calculate how many segments are needed
        // for our arc.
        segs = Math.ceil(Math.abs(arc) / 45);
        // Now calculate the sweep of each segment.
        segAngle_a = arc / segs;
        segAngle_b = -arc / segs;
        // The math requires radians rather than degrees. To convert from degrees
        // use the formula (degrees/180)*Math.PI to get radians.
        theta_a = - (segAngle_a / 180) * Math.PI;
        theta_b = - (segAngle_b / 180) * Math.PI;
        // convert angle startAngle to radians
        angle = - (startAngle / 180) * Math.PI;
        // draw the curve in segments no larger than 45 degrees.
        if (segs > 0) {
            // draw a line from the end of the interior curve to the start of the exterior curve
            ax = x + Math.sin( - startAngle / 180 * Math.PI) * radius;
            ay = y + Math.cos( startAngle / 180 * Math.PI) * radius;
            mc.moveTo((float) ax, (float)ay);
            // Loop for drawing exterior  curve segments
            for (int i = 0; i < segs; i ++) {
                angle += theta_a;
                angleMid = angle - (theta_a / 2);
                bx = x + Math.sin(angle) * radius;
                by = y + Math.cos(angle) * radius;
                cx = x + Math.sin(angleMid) * (radius / Math.cos(theta_a / 2));
                cy = y + Math.cos(angleMid) * (radius / Math.cos(theta_a / 2));
                mc.quadTo((float)cx, (float)cy, (float)bx, (float)by);
            }
        }
    }
    
    /**
     * Draws the Sunburst tree onto
     * the supplied graphics object.
     */
    public void drawTree(Graphics2D g) {
        drawTree(g, root);
    }
    
    public void drawTree(Graphics2D g, SunburstNode node) {
        drawNode(g, node);
        drawLabel(g, node);
        for (SunburstNode child : node.children()) {
            drawTree(g, child);
        }
    }
    
    public void drawContours(Graphics2D g, SunburstNode node, Color color) {
        GeneralPath path = new GeneralPath();
        addArc(path, node);
        g.setColor(color);
        g.draw(path);
    }
    
    private void addArc(GeneralPath path, SunburstNode node) {
        /*
        if (! node.isLeaf()) {
            double ro = getRadius(node.getDepth() - root.getDepth() + 1);
            double startAngle = (node.getLeft() - root.getLeft()) * numberToAngleFactor;
            double arc = node.getExtent() * numberToAngleFactor;
            addSeg(path, cx, cy,
                    startAngle / Math.PI * 180, arc / Math.PI * 180,
                    ro
                    );
        }
        for (SunburstNode child : node.children()) {
            addArc(path, child);
        }*/
    }
    
    public void drawDescendants(Graphics2D g, SunburstNode node) {
        for (SunburstNode child : node.children()) {
            drawTree(g, child);
        }
    }
    private double getRadius(int depth) {
        return cx +
                width * depth / (double) totalDepth;
    }
    /**
     * Converts screen coordinates to polar coordinates in degrees.
     */
    public double getTheta(double x, double y) {
        if (y < cy || y > cy + height) {
            return 0;
        }
        return (y - cy) * 360d / height;
    }
    
    
    public void drawLabel(Graphics2D g, SunburstNode node) {
        double h = height * node.getExtent() / root.getExtent();
        double less;
        if (h > 2 && node.getExtent() < root.getExtent()) {
            less = 0.5;
        } else {
            less = 0;
        }
        
        Rectangle2D.Double rec = new Rectangle2D.Double(
                cx + width * (node.getDepth() - root.getDepth()) / totalDepth,
                cy + height * (node.getLeft() - root.getLeft()) / root.getExtent() + less,
                width / totalDepth - 1,
                height * node.getExtent() / root.getExtent() - less * 2
                );
        //g.setColor(info.getColor(node.getDataNodePath()));
        
        FontMetrics fm = g.getFontMetrics();
        int fh = fm.getHeight();
        if (fh < rec.height) {
            double space = rec.width - 4;
            
            String name = info.getName(node.getDataNodePath());
            char[] nameC = (name == null) ? new char[0] : name.toCharArray();
            int nameLength = nameC.length;
            int nameWidth = fm.charsWidth(nameC, 0, nameLength);
            
            while((nameWidth >= space) && (nameLength > 1)) {
                nameLength--;
                nameC[nameLength - 1] = '·';
                nameWidth = fm.charsWidth(nameC, 0, nameLength);
            }
            
            if (nameLength > 1 || nameLength == nameC.length) {
                g.setColor(Color.BLACK);
                g.drawString(new String(nameC, 0, nameLength),
                        (int) rec.x + 4,
                        (int) (rec.y + fm.getAscent() + (rec.height - fh) / 2));
            }
            
        }
    }
    public void drawNode(Graphics2D g, SunburstNode node) {
        double h = height * node.getExtent() / root.getExtent();
        double less;
        if (h > 2 && node.getExtent() < root.getExtent()) {
            less = 0.5;
        } else {
            less = 0;
        }
        
        Rectangle2D.Double r = new Rectangle2D.Double(
                cx + width * (node.getDepth() - root.getDepth()) / totalDepth,
                cy + height * (node.getLeft() - root.getLeft()) / root.getExtent() + less,
                width / totalDepth - 1,
                height * node.getExtent() / root.getExtent() - less * 2
                );
        g.setColor(info.getColor(node.getDataNodePath()));
        g.fill(r);
    }

   public NodeInfo getInfo() {
        return info;
    }
}
