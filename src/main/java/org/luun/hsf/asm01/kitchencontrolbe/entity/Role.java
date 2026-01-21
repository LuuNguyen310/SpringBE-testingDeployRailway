package org.luun.hsf.asm01.kitchencontrolbe.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "roles")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int roleId;

    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    //Trong role ko cần tham chiếu quan hệ @OneToMany vì ta ko cần lấy các User
    //từ 1 role cụ thể (có thể là có nhưng rất hiếm)
}
