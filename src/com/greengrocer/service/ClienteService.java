package com.greengrocer.service;

import com.greengrocer.dao.ClienteDao;
import com.greengrocer.exception.BusinessException;
import com.greengrocer.exception.DataAccessException;
import com.greengrocer.model.Cliente;
import com.greengrocer.util.ValidationUtils;

import java.util.List;

public class ClienteService {

    private final ClienteDao dao = new ClienteDao();

    public Cliente crear(Cliente c) {
        validar(c);
        try { return dao.crear(c); }
        catch (DataAccessException ex) { return traducirDuplicado(ex); }
    }

    public void actualizar(Cliente c) {
        if (c == null || c.getId() == 0) throw new BusinessException("Seleccione un cliente.");
        validar(c);
        try { dao.actualizar(c); }
        catch (DataAccessException ex) { traducirDuplicado(ex); }
    }

    public void eliminar(int id) {
        try { dao.eliminar(id); }
        catch (DataAccessException ex) {
            if (ex.getMessage().toLowerCase().contains("foreign")) {
                throw new BusinessException(
                        "No se puede eliminar: el cliente tiene ventas registradas.");
            }
            throw ex;
        }
    }

    public List<Cliente> listar()        { return dao.listar(); }
    public List<Cliente> listarActivos() { return dao.listarActivos(); }
    public List<Cliente> buscar(String q) {
        return (q == null || q.isBlank()) ? listarActivos() : dao.buscarPorTexto(q.trim());
    }

    private void validar(Cliente c) {
        c.setNombre(ValidationUtils.requireText(c.getNombre(), "Nombre"));
        c.setDni(ValidationUtils.optional(c.getDni()));
        c.setTelefono(ValidationUtils.optional(c.getTelefono()));
        c.setEmail(ValidationUtils.optionalEmail(c.getEmail()));
        c.setDireccion(ValidationUtils.optional(c.getDireccion()));
    }

    private Cliente traducirDuplicado(DataAccessException ex) {
        if (ex.getMessage().toLowerCase().contains("duplicate")) {
            throw new BusinessException("Ya existe un cliente con ese DNI.");
        }
        throw ex;
    }
}
