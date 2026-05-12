package org.example.mapper;

import java.util.List;

// Интерфейс использует дженерики (обобщения) <T>, чтобы работать с любым классом
public interface DataMapper<T> {
    void insert(T entity);
    T findById(int id);
    List<T> findAll();
}