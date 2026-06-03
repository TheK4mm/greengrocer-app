package com.greengrocer.service;

import com.greengrocer.dao.ProveedorDao;
import com.greengrocer.exception.BusinessException;
import com.greengrocer.exception.DataAccessException;
import com.greengrocer.model.Proveedor;
import com.greengrocer.util.ValidationUtils;

import java.util.List;

public class ProveedorService {

    private final ProveedorDao dao = new ProveedorDao();

    public Proveedor crear(Proveedor p) {
        validar(p);
        try { return dao.crear(p); }
        catch (DataAccessException ex) { return traducirDuplicado(ex); }
    }

    public void actualizar(Proveedor p) {
        if (p == null || p.getId() == 0) throw new BusinessException("Seleccione un proveedor.");
        validar(p);
        try { dao.actualizar(p); }
        catch (DataAccessException ex) { traducirDuplicado(ex); }
    }

    public void eliminar(int id) {
        try { dao.eliminar(id); }
        catch (DataAccessException ex) {
            if (ex.getMessage().toLowerCase().contains("foreign")) {
                throw new BusinessException(
                        "No se puede eliminar: existen productos vinculados a este proveedor.");
            }
            throw ex;
        }
    }

    public List<Proveedor> listar()        { return dao.listar(); }
    public List<Proveedor> listarActivos() { return dao.listarActivos(); }

    private void validar(Proveedor p) {
        p.setNombre(ValidationUtils.requireText(p.getNombre(), "Nombre"));
        p.setRuc(ValidationUtils.optional(p.getRuc()));
        p.setTelefono(ValidationUtils.optional(p.getTelefono()));
        p.setEmail(ValidationUtils.optionalEmail(p.getEmail()));
        p.setDireccion(ValidationUtils.optional(p.getDireccion()));
    }

    private Proveedor traducirDuplicado(DataAccessException ex) {
        if (ex.getMessage().toLowerCase().contains("duplicate")) {
            throw new BusinessException("Ya existe un proveedor con ese RUC.");
        }
        throw ex;
    }
}
