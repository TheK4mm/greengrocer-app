package com.greengrocer.view.components;

import com.greengrocer.view.theme.ColorPalette;
import com.greengrocer.view.theme.FontPalette;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.awt.Component;

/** JTable con estilo corporativo: filas alternadas, header verde, sin grilla. */
public class AppTable extends JTable {

    public AppTable(TableModel model) {
        super(model);
        applyStyle();
    }

    private void applyStyle() {
        setRowHeight(34);
        setFont(FontPalette.TABLE);
        setForeground(ColorPalette.TEXT_PRIMARY);
        setBackground(ColorPalette.CARD_BG);
        setShowGrid(false);
        setIntercellSpacing(new java.awt.Dimension(0, 0));
        setSelectionBackground(new Color(0xE8F5E9));
        setSelectionForeground(ColorPalette.TEXT_PRIMARY);
        setFillsViewportHeight(true);
        setAutoCreateRowSorter(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setGridColor(ColorPalette.DIVIDER);

        UIManager.put("Table.alternateRowColor", new Color(0xFAFAFA));

        JTableHeader header = getTableHeader();
        header.setBackground(ColorPalette.HEADER_BG);
        header.setForeground(ColorPalette.PRIMARY_DARK);
        header.setFont(FontPalette.TABLE_HEAD);
        header.setPreferredSize(new java.awt.Dimension(getWidth(), 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPalette.PRIMARY));
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.LEFT);

        DefaultTableCellRenderer cell = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean sel, boolean focus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, focus, r, c);
                lbl.setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!sel) {
                    lbl.setBackground(r % 2 == 0 ? Color.WHITE : new Color(0xFAFAFA));
                }
                return lbl;
            }
        };
        setDefaultRenderer(Object.class, cell);
    }

    public void setRenderer(int columnIndex, TableCellRenderer renderer) {
        getColumnModel().getColumn(columnIndex).setCellRenderer(renderer);
    }
}
