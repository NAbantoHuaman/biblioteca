package pe.edu.idat.biblioteca.service.impl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.idat.biblioteca.exception.ResourceNotFoundException;
import pe.edu.idat.biblioteca.service.CrudService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase base para la implementación de servicios CRUD genéricos.
 * Utiliza el patrón Template Method para delegar el mapeo y la obtención del repositorio.
 */
public abstract class BaseServiceImpl<E, RE, RS, ID> implements CrudService<RE, RS, ID> {

    protected abstract JpaRepository<E, ID> getRepository();
    protected abstract RS mapToResponse(E entity);
    protected abstract E mapToEntity(RE request);
    protected abstract void updateEntityFromRequest(RE request, E entity);
    protected abstract String getEntityName();

    @Override
    @Transactional(readOnly = true)
    public List<RS> listarTodos() {
        return getRepository().findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RS obtenerPorId(ID id) {
        E entity = getRepository().findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getEntityName(), "id", id));
        return mapToResponse(entity);
    }

    @Override
    @Transactional
    public RS crear(RE request) {
        E entity = mapToEntity(request);
        entity = getRepository().save(entity);
        return mapToResponse(entity);
    }

    @Override
    @Transactional
    public RS actualizar(ID id, RE request) {
        E entity = getRepository().findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getEntityName(), "id", id));
        updateEntityFromRequest(request, entity);
        entity = getRepository().save(entity);
        return mapToResponse(entity);
    }

    @Override
    @Transactional
    public void eliminar(ID id) {
        if (!getRepository().existsById(id)) {
            throw new ResourceNotFoundException(getEntityName(), "id", id);
        }
        getRepository().deleteById(id);
    }
}
