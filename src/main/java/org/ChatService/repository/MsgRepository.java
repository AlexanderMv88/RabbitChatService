package org.ChatService.repository;

import org.ChatService.entity.Employee;
import org.ChatService.entity.Msg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MsgRepository extends JpaRepository<Msg, Long> {
    List<Msg> findBySender(Employee employee);
}
