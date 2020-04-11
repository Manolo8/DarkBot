package com.github.manolo8.darkbot.view.draw;

import com.github.manolo8.darkbot.core.manager.MapManager;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

public class GraphicDrawer {

    private final MapManager mapManager;

    protected GraphicsContext graphics;
    private   FontLoader      fontLoader;

    public  double width;
    public  double height;
    private double ratioX;
    private double ratioY;

    public double x;
    public double y;

    private boolean translate;
    private Paint   color;

    public GraphicDrawer(MapManager mapManager, GraphicsContext graphicsContext2D) {
        this.mapManager = mapManager;

        fontLoader = Toolkit.getToolkit().getFontLoader();
        graphics = graphicsContext2D;
    }

    public void setWidth(Number width) {
        this.width = (double) width;
        this.ratioX = this.width / mapManager.internalWidth;
    }

    public void setHeight(Number height) {
        this.height = (double) height;
        this.ratioY = this.height / mapManager.internalHeight;
    }

    public void setTranslate(boolean translate) {
        this.translate = translate;
    }

    public void move(double x, double y) {

        if (translate) {
            x = x * ratioX;
            y = y * ratioY;
        }

        this.x += x;
        this.y += y;
    }

    public void set(double x, double y) {

        if (translate) {
            x = x * ratioX;
            y = y * ratioY;
        }

        this.x = x;
        this.y = y;
    }

    public void drawLineTo(double tx, double ty) {
        double ox = x;
        double oy = y;

        set(tx, ty);

        graphics.beginPath();
        graphics.setStroke(color);
        graphics.moveTo(ox, oy);
        graphics.lineTo(x, y);
        graphics.stroke();
        graphics.closePath();
    }

    public void setColor(Paint paint) {
        this.color = paint;
    }

    public void fill(Paint paint) {
        setColor(paint);

        graphics.beginPath();
        this.graphics.setFill(color);
        graphics.fillRect(0, 0, width, height);
        graphics.closePath();
    }

    public void setFont(Font font) {
        graphics.setFont(font);
    }

    public void drawStringCenter(String str) {
        double fontWidth = fontLoader.computeStringWidth(str, graphics.getFont());

        graphics.beginPath();
        this.graphics.setFill(color);
        graphics.fillText(str, x - (fontWidth / 2), y - (graphics.getFont().getSize() / 2));
        graphics.closePath();
    }

    public void drawStringRight(String str) {
        graphics.beginPath();
        this.graphics.setFill(color);
        graphics.fillText(str, x, y - graphics.getFont().getSize() / 2);
        graphics.closePath();
    }

    public void drawStringLeft(String str) {
        double fontWidth = fontLoader.computeStringWidth(str, graphics.getFont());

        graphics.beginPath();
        this.graphics.setFill(color);
        graphics.fillText(str, x - fontWidth, y - graphics.getFont().getSize() / 2);
        graphics.closePath();
    }

    public void drawOvalCenter(double width, double height) {
        graphics.beginPath();
        this.graphics.setStroke(color);
        graphics.strokeOval(x - ((width - 1) / 2), y - ((height - 1) / 2), width, height);
        graphics.closePath();
    }

    public void fillOvalCenter(double width, double height) {
        graphics.beginPath();
        this.graphics.setFill(color);
        graphics.fillOval(x - ((width - 1) / 2), y - ((height - 1) / 2), width, height);
        graphics.closePath();
    }

    public void drawRectCenter(double width, double height) {
        graphics.beginPath();
        this.graphics.setStroke(color);
        graphics.strokeRect(x - ((width - 1) / 2), y - ((height - 1) / 2), width, height);
        graphics.closePath();
    }

    public void fillRectCenter(double width, double height) {
        graphics.beginPath();
        this.graphics.setFill(color);
        graphics.fillRect(x - ((width - 1) / 2), y - ((height - 1) / 2), width, height);
        graphics.closePath();
    }

    public void fillRect(double width, double height) {
        graphics.beginPath();
        this.graphics.setFill(color);
        graphics.fillRect(x, y, width, height);
        graphics.closePath();
    }

    public void drawRect(double width, double height) {
        graphics.beginPath();
        this.graphics.setStroke(color);
        graphics.strokeRect(x, y, width, height);
        graphics.closePath();
    }

    public void fillProgress(Paint back, Paint front, double width, double height, double percent) {
        setColor(back);
        fillRect(width, height);
        setColor(front);
        fillRect(width * percent, height);
    }

    public double translateX(double value) {
        return value * ratioX;
    }

    public double translateY(double value) {
        return value * ratioY;
    }

    public double undoTranslateX(double x) {
        return (x / ratioX);
    }

    public double undoTranslateY(double y) {
        return (y / ratioY);
    }

    public void reset() {

        this.x = 0;
        this.y = 0;

        this.ratioX = this.width / mapManager.internalWidth;
        this.ratioY = this.height / mapManager.internalHeight;

        fill(Palette.BACKGROUND);
    }

    public double midX() {
        return width / 2;
    }

    public double midY() {
        return height / 2;
    }
}
