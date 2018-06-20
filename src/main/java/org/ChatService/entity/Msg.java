package org.ChatService.entity;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class Msg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String text;
    @JoinColumn(name = "sender_employee_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Employee sender;

    public Msg(String text, Employee sender) {
        this.text = text;
        this.sender = sender;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Msg msg = (Msg) o;
        return id == msg.id &&
                Objects.equals(text, msg.text);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, text);
    }
}
