package com.greengrocer.view.icons;

import javax.swing.Icon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Conjunto de íconos vectoriales dibujados con Java2D (sin dependencias
 * externas). Son nítidos a cualquier tamaño —como un SVG— y heredan el color
 * que se les pase, de modo que combinan con el tema de la aplicación.
 *
 * <p>Uso típico: {@code new VectorIcon(VectorIcon.Glyph.CART, 18, color)}.</p>
 */
public final class VectorIcon implements Icon {

    /** Figuras disponibles, una por módulo/acción de la aplicación. */
    public enum Glyph {
        HOME, CART, BOX, TAG, USER, USERS, TRUCK, RECEIPT, CHART, LOGOUT, LEAF, EYE, EYE_OFF
    }

    private final Glyph glyph;
    private final int   size;
    private final Color color;

    public VectorIcon(Glyph glyph, int size, Color color) {
        this.glyph = glyph;
        this.size  = size;
        this.color = color;
    }

    @Override public int getIconWidth()  { return size; }
    @Override public int getIconHeight() { return size; }

    /** Rasteriza el ícono a una imagen ARGB (útil para {@code Frame.setIconImage}). */
    public BufferedImage toImage() {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        paintIcon(null, g, 0, 0);
        g.dispose();
        return img;
    }

    @Override public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.translate(x, y);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(Math.max(1.4f, size / 11f),
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Caja interior: dejamos un margen para que el trazo no se recorte.
        double p = size * 0.14;          // padding
        double s = size - 2 * p;         // span útil

        switch (glyph) {
            case HOME:    paintHome(g2, p, s);    break;
            case CART:    paintCart(g2, p, s);    break;
            case BOX:     paintBox(g2, p, s);     break;
            case TAG:     paintTag(g2, p, s);     break;
            case USER:    paintUser(g2, p, s);    break;
            case USERS:   paintUsers(g2, p, s);   break;
            case TRUCK:   paintTruck(g2, p, s);   break;
            case RECEIPT: paintReceipt(g2, p, s); break;
            case CHART:   paintChart(g2, p, s);   break;
            case LOGOUT:  paintLogout(g2, p, s);  break;
            case LEAF:    paintLeaf(g2, p, s);    break;
            case EYE:     paintEye(g2, p, s);     break;
            case EYE_OFF: paintEyeOff(g2, p, s);  break;
        }
        g2.dispose();
    }

    // ----------------------------------------------------------------- figuras

    private void paintHome(Graphics2D g, double p, double s) {
        g.draw(poly(p, s, false, 0.10,0.50, 0.50,0.14, 0.90,0.50));   // techo
        g.draw(poly(p, s, false, 0.22,0.46, 0.22,0.90, 0.78,0.90, 0.78,0.46)); // cuerpo
        g.draw(poly(p, s, false, 0.43,0.90, 0.43,0.66, 0.57,0.66, 0.57,0.90)); // puerta
    }

    private void paintCart(Graphics2D g, double p, double s) {
        g.draw(poly(p, s, false,
                0.08,0.18, 0.24,0.18, 0.40,0.62, 0.86,0.62, 0.94,0.32, 0.30,0.32));
        g.draw(circle(p, s, 0.46, 0.82, 0.07));
        g.draw(circle(p, s, 0.80, 0.82, 0.07));
    }

    private void paintBox(Graphics2D g, double p, double s) {
        g.draw(poly(p, s, true, 0.18,0.36, 0.82,0.36, 0.82,0.88, 0.18,0.88)); // cuerpo
        g.draw(poly(p, s, false, 0.18,0.36, 0.50,0.20, 0.82,0.36));           // tapa
        g.draw(poly(p, s, false, 0.50,0.20, 0.50,0.88));                      // cinta
    }

    private void paintTag(Graphics2D g, double p, double s) {
        g.draw(poly(p, s, true, 0.20,0.20, 0.56,0.20, 0.86,0.50, 0.50,0.86, 0.20,0.56));
        g.draw(circle(p, s, 0.36, 0.36, 0.05)); // ojal
    }

    private void paintUser(Graphics2D g, double p, double s) {
        g.draw(circle(p, s, 0.50, 0.34, 0.17));
        Path2D.Double shoulders = new Path2D.Double();
        shoulders.moveTo(p + s * 0.18, p + s * 0.88);
        shoulders.curveTo(p + s * 0.20, p + s * 0.60,
                          p + s * 0.80, p + s * 0.60,
                          p + s * 0.82, p + s * 0.88);
        g.draw(shoulders);
    }

    private void paintUsers(Graphics2D g, double p, double s) {
        // Persona de atrás (derecha)
        g.draw(circle(p, s, 0.68, 0.34, 0.13));
        Path2D.Double back = new Path2D.Double();
        back.moveTo(p + s * 0.58, p + s * 0.66);
        back.curveTo(p + s * 0.74, p + s * 0.56,
                     p + s * 0.92, p + s * 0.64,
                     p + s * 0.90, p + s * 0.88);
        g.draw(back);
        // Persona de adelante (izquierda)
        g.draw(circle(p, s, 0.38, 0.40, 0.15));
        Path2D.Double front = new Path2D.Double();
        front.moveTo(p + s * 0.12, p + s * 0.88);
        front.curveTo(p + s * 0.14, p + s * 0.62,
                      p + s * 0.62, p + s * 0.62,
                      p + s * 0.64, p + s * 0.88);
        g.draw(front);
    }

    private void paintTruck(Graphics2D g, double p, double s) {
        g.draw(poly(p, s, true, 0.10,0.40, 0.60,0.40, 0.60,0.72, 0.10,0.72)); // furgón
        g.draw(poly(p, s, true, 0.60,0.50, 0.76,0.50, 0.90,0.62, 0.90,0.72, 0.60,0.72)); // cabina
        g.draw(circle(p, s, 0.28, 0.78, 0.08));
        g.draw(circle(p, s, 0.76, 0.78, 0.08));
    }

    private void paintReceipt(Graphics2D g, double p, double s) {
        g.draw(poly(p, s, true,
                0.26,0.14, 0.74,0.14, 0.74,0.84, 0.66,0.78, 0.58,0.84,
                0.50,0.78, 0.42,0.84, 0.34,0.78, 0.26,0.84));
        g.draw(poly(p, s, false, 0.36,0.36, 0.64,0.36)); // línea de texto
        g.draw(poly(p, s, false, 0.36,0.52, 0.64,0.52)); // línea de texto
    }

    private void paintChart(Graphics2D g, double p, double s) {
        g.draw(poly(p, s, false, 0.18,0.14, 0.18,0.86, 0.88,0.86)); // ejes
        g.fill(new Rectangle2D.Double(p + s * 0.28, p + s * 0.58, s * 0.11, s * 0.28));
        g.fill(new Rectangle2D.Double(p + s * 0.46, p + s * 0.40, s * 0.11, s * 0.46));
        g.fill(new Rectangle2D.Double(p + s * 0.64, p + s * 0.50, s * 0.11, s * 0.36));
    }

    private void paintLogout(Graphics2D g, double p, double s) {
        g.draw(poly(p, s, false, 0.50,0.16, 0.20,0.16, 0.20,0.84, 0.50,0.84)); // marco/puerta
        g.draw(poly(p, s, false, 0.44,0.50, 0.86,0.50));                       // flecha
        g.draw(poly(p, s, false, 0.70,0.36, 0.86,0.50, 0.70,0.64));            // punta
    }

    private void paintLeaf(Graphics2D g, double p, double s) {
        Path2D.Double leaf = new Path2D.Double();
        leaf.moveTo(p + s * 0.22, p + s * 0.80);
        leaf.curveTo(p + s * 0.16, p + s * 0.40,
                     p + s * 0.50, p + s * 0.16,
                     p + s * 0.84, p + s * 0.22);
        leaf.curveTo(p + s * 0.74, p + s * 0.58,
                     p + s * 0.48, p + s * 0.82,
                     p + s * 0.22, p + s * 0.80);
        leaf.closePath();
        g.draw(leaf);
        g.draw(poly(p, s, false, 0.30,0.72, 0.74,0.32)); // nervadura central
    }

    private void paintEye(Graphics2D g, double p, double s) {
        Path2D.Double eye = new Path2D.Double();
        eye.moveTo(p + s * 0.06, p + s * 0.50);
        eye.quadTo(p + s * 0.50, p + s * 0.18, p + s * 0.94, p + s * 0.50);
        eye.quadTo(p + s * 0.50, p + s * 0.82, p + s * 0.06, p + s * 0.50);
        eye.closePath();
        g.draw(eye);
        g.draw(circle(p, s, 0.50, 0.50, 0.15)); // pupila
    }

    private void paintEyeOff(Graphics2D g, double p, double s) {
        paintEye(g, p, s);
        g.draw(poly(p, s, false, 0.14, 0.14, 0.86, 0.86)); // tachado diagonal
    }

    // ----------------------------------------------------------------- helpers

    /** Construye una polilínea (opcionalmente cerrada) a partir de pares x,y normalizados [0,1]. */
    private static Path2D.Double poly(double p, double s, boolean close, double... n) {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(p + s * n[0], p + s * n[1]);
        for (int i = 2; i < n.length; i += 2) {
            path.lineTo(p + s * n[i], p + s * n[i + 1]);
        }
        if (close) path.closePath();
        return path;
    }

    /** Círculo con centro y radio normalizados [0,1]. */
    private static Ellipse2D.Double circle(double p, double s, double cx, double cy, double r) {
        return new Ellipse2D.Double(p + s * (cx - r), p + s * (cy - r), s * 2 * r, s * 2 * r);
    }
}
