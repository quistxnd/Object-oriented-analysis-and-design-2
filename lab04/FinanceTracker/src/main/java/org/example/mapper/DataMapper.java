package org.example.mapper;

import java.util.List;

public interface DataMapper<T> {
    void insert(T entity);
    T findById(int id);
    List<T> findAll();
}
