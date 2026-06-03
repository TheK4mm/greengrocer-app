package com.greengrocer.service;

import com.greengrocer.dao.ProductoDao;
import com.greengrocer.dao.VentaDao;

import java.math.BigDecimal;
import java.util.List;

/** Datos resumidos para el dashboard y la pantalla de reportes. */
public class ReporteService {

    private final ProductoDao productoDao = new ProductoDao();
    private final VentaDao    ventaDao    = new VentaDao();

    public int productosActivos()    { return productoDao.contarActivos(); }
    public int stockCritico()        { return productoDao.contarStockCritico(); }
    public int ventasDelDia()        { return ventaDao.contarVentasDelDia(); }
    public BigDecimal ingresosDelDia() { return ventaDao.totalIngresosDelDia(); }
    public BigDecimal ingresosDelMes() { return ventaDao.totalIngresosDelMes(); }
    public List<Object[]> topProductos(int n) { return ventaDao.topProductosVendidos(n); }
}
