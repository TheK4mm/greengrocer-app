package com.greengrocer.service;

import com.greengrocer.dao.ProductoDao;
import com.greengrocer.exception.BusinessException;
import com.greengrocer.exception.DataAccessException;
import com.greengrocer.model.Producto;
import com.greengrocer.util.ValidationUtils;

import java.util.List;

public class ProductoService {

    private final ProductoDao dao = new ProductoDao();

    public Producto crear(Producto p) {
        validar(p);
        try { return dao.crear(p); }
        catch (DataAccessException ex) { return traducirDuplicado(ex); }
    }

    public void actualizar(Producto p) {
        if (p == null || p.getId() == 0) throw new BusinessException("Seleccione un producto.");
        validar(p);
        try { dao.actualizar(p); }
        catch (DataAccessException ex) { traducirDuplicado(ex); }
    }

    public void eliminar(int id) {
        try { dao.eliminar(id); }
        catch (DataAccessException ex) {
            if (ex.getMessage().toLowerCase().contains("foreign")) {
                throw new BusinessException(
                        "No se puede eliminar: el producto tiene ventas asociadas.");
            }
            throw ex;
        }
    }

    public List<Producto> listar()                  { return dao.listar(); }
    public List<Producto> listarActivos()           { return dao.listarActivos(); }
    public List<Producto> listarStockCritico()      { return dao.listarStockCritico(); }
    public List<Producto> buscar(String q) {
        return (q == null || q.isBlank()) ? listarActivos() : dao.buscarPorTexto(q.trim());
    }

    public int contarActivos()       { return dao.contarActivos(); }
    public int contarStockCritico()  { return dao.contarStockCritico(); }

    private void validar(Producto p) {
        p.setCodigo(ValidationUtils.requireText(p.getCodigo(), "Código"));
        p.setNombre(ValidationUtils.requireText(p.getNombre(), "Nombre"));
        p.setDescripcion(ValidationUtils.optional(p.getDescripcion()));
        if (p.getPrecioVenta() == null || p.getPrecioVenta().signum() < 0) {
            throw new BusinessException("El precio de venta no puede ser negativo.");
        }
        if (p.getPrecioCompra() == null) p.setPrecioCompra(java.math.BigDecimal.ZERO);
        if (p.getPrecioCompra().signum() < 0) {
            throw new BusinessException("El precio de compra no puede ser negativo.");
        }
        if (p.getStock() < 0) {
            throw new BusinessException("El stock no puede ser negativo.");
        }
        if (p.getStockMinimo() < 0) {
            throw new BusinessException("El stock mínimo no puede ser negativo.");
        }
        if (p.getIdCategoria() <= 0) {
            throw new BusinessException("Debe seleccionar una categoría.");
        }
        if (p.getUnidadMedida() == null || p.getUnidadMedida().isBlank()) {
            p.setUnidadMedida("unidad");
        }
    }

    private Producto traducirDuplicado(DataAccessException ex) {
        if (ex.getMessage().toLowerCase().contains("duplicate")) {
            throw new BusinessException("Ya existe un producto con ese código.");
        }
        throw ex;
    }
}
