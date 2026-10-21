package com.showticket.show.service;

public interface IShowDocumentService {

    void createIndexIfNotExists();

    void importAll();

    long count();

    long reindex();
}
