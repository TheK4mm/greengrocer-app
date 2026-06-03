package com.greengrocer.service;

import com.greengrocer.dao.CategoriaDao;
import com.greengrocer.exception.BusinessException;
import com.greengrocer.exception.DataAccessException;
import com.greengrocer.model.Categoria;
import com.greengrocer.util.ValidationUtils;

import java.util.List;

public class CategoriaService {

    private final CategoriaDao dao = new CategoriaDao();

    public Categoria crear(String nombre, String descripcion) {
        Categoria c = new Categoria();
        c.setNombre(ValidationUtils.requireText(nombre, "Nombre"));
        c.setDescripcion(ValidationUtils.optional(descripcion));
        c.setActivo(true);
        try { return dao.crear(c); }
        catch (DataAccessException ex) {
            if (ex.getMessage().toLowerCase().contains("duplicate")) {
                throw new BusinessException("Ya existe una categoría con ese nombre.");
            }
            throw ex;
        }
    }

    public void actualizar(Categoria c) {
        if (c == null || c.getId() == 0) throw new BusinessException("Seleccione una categoría.");
        c.setNombre(ValidationUtils.requireText(c.getNombre(), "Nombre"));
        c.setDescripcion(ValidationUtils.optional(c.getDescripcion()));
        try { dao.actualizar(c); }
        catch (DataAccessException ex) {
            if (ex.getMessage().toLowerCase().contains("duplicate")) {
                throw new BusinessException("Ya existe una categoría con ese nombre.");
            }
            throw ex;
        }
    }

    public void eliminar(int id) {
        try { dao.eliminar(id); }
        catch (DataAccessException ex) {
            if (ex.getMessage().toLowerCase().contains("foreign")) {
                throw new BusinessException(
                        "No se puede eliminar: hay productos asignados a esta categoría.");
            }
            throw ex;
        }
    }

    public List<Categoria> listar()         { return dao.listar(); }
    public List<Categoria> listarActivas()  { return dao.listarActivas(); }
}
