package com.example.applicationcongess.repositories;

import com.example.applicationcongess.models.Messages;
import org.springframework.data.repository.CrudRepository;


public interface MessagesRepository extends CrudRepository<Messages, Long> {
}
