package com.greengrocer.dao;

import java.util.List;
import java.util.Optional;

/**
 * Contrato CRUD genérico.
 *
 * @param <T>  Tipo de entidad
 * @param <ID> Tipo de la clave primaria
 */
public interface GenericDao<T, ID> {

    T crear(T entidad);

    boolean actualizar(T entidad);

    boolean eliminar(ID id);

    Optional<T> buscarPorId(ID id);

    List<T> listar();
}
