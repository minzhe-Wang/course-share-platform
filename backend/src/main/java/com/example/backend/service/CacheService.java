package com.example.backend.service;

public interface CacheService {

    void evictHotMaterials();

    void evictHotQuestions();

    void evictAllHotContent();
}
